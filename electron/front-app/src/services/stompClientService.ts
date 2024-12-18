import { Message } from "@stomp/stompjs";
import { handleContainerCommand } from "./deployments/deployCommandHandler";
import {
  startSendingCurrentState,
  stopContainerStatsMonitoring,
  stopSendingCurrentState,
} from "./monitoring/healthCheckPingUtils";
import { client } from "./websocket/stompClientUtils";
import { sendComputeConnectMessage } from "./websocket/sendComputeConnect";
import { useAppStore } from "../stores/appStatusStore";
import { handleDockerBuild } from "./deployments/buildHandler/buildDeployHandler";
import { handleDatabaseBuild } from "./deployments/buildHandler/buildDatabaseHandler";
import { useContainerStore } from "../stores/containerStore";
import { useImageStore } from "../stores/imageStore";
import { DeployStatus } from "../types/deploy";


const setWebsocketStatus = useAppStore.getState().setWebsocketStatus;
const setServiceStatus = useAppStore.getState().setServiceStatus;
const { createContainerEntry, updateContainerInfo } =
  useContainerStore.getState();
const { createImageEntry, updateImageInfo } = useImageStore.getState();



export function setupClientHandlers(userId: string): void {
  client.onConnect = (frame) => {
    console.log("Connected: " + frame);
    setWebsocketStatus("connected");
    // WebSocket 연결 시, 컴퓨트 연결 메시지 및 결제 정보 전송
    sendComputeConnectMessage(userId);
    startSendingCurrentState(userId); // 배포 상태 PING 전송 시작
    setServiceStatus("running");

    // compute-create 구독/Frontend & Backend 배포
    client.subscribe(
      `/sub/compute-create/${userId}`,
      async (message: Message) => {
        const deployCreate: DeploymentCreateEvent = JSON.parse(message.body);
        const { instance } = deployCreate;
        const serviceId = `${instance.serviceType}-${instance.deploymentId}`;
        const imageStore = useImageStore.getState();
        const containerStore = useContainerStore.getState();

        // 이미지 처리
        if (!imageStore.images.some((image) => image.id === serviceId)) {
          createImageEntry(
            deployCreate.instance.serviceType,
            deployCreate.instance.deploymentId
          );
        }

        // 컨테이너 처리
        if (
          !containerStore.containers.some(
            (container) => container.id === serviceId
          )
        ) {
          createContainerEntry(
            deployCreate.instance.serviceType,
            deployCreate.instance.deploymentId
          );
        }

        updateImageInfo(serviceId, { status: DeployStatus.WAITING });
        updateContainerInfo(serviceId, {
          status: DeployStatus.WAITING,
        });

        console.log(`생성요청 도착`, deployCreate);
        await handleDockerBuild(deployCreate);
      }
    );

    //database-create 구독 / database 배포
    client.subscribe(
      `/sub/database-create/${userId}`,
      async (message: Message) => {
        const dbCreate: DatabaseCreateEvent = JSON.parse(message.body);
        const { instance } = dbCreate;
        const serviceId = `${instance.serviceType}-${instance.databaseId}`;
        createImageEntry(
          dbCreate.instance.serviceType,
          dbCreate.instance.databaseId
        );
        createContainerEntry(
          dbCreate.instance.serviceType,
          dbCreate.instance.databaseId
        );
        updateImageInfo(serviceId, { status: DeployStatus.WAITING });
        updateContainerInfo(serviceId, { status: DeployStatus.WAITING });
        console.log(`생성요청 도착`, dbCreate);
        await handleDatabaseBuild(dbCreate);
      }
    );

    // compute-update 구독 / command 처리
    client.subscribe(
      `/sub/compute-update/${userId}`,
      async (message: Message) => {
        try {
          const { serviceType, id, command } = JSON.parse(message.body);
          const serviceId = `${serviceType}-${id}`;
          updateContainerInfo(serviceId, { status: DeployStatus.WAITING });
          handleContainerCommand(serviceType, serviceId, command); // 컨테이너 명령 처리
        } catch (error) {
          console.error("Error processing compute update message:", error);
        }
      }
    );

    // WebSocket 오류 처리
    client.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
      setWebsocketStatus("disconnected");
    };

    client.onWebSocketClose = (event: CloseEvent) => {
      console.warn("WebSocket connection closed:", event);
      setWebsocketStatus("disconnected");
    };

    // WebSocket 연결 해제 처리
    client.onDisconnect = () => {
      window.electronAPI.showMessageBox("연결이 해제 되었습니다. 재시도 해주세요")
      console.log("Websocket Disconnected....");
      setWebsocketStatus("disconnected");
      stopContainerStatsMonitoring();
      stopSendingCurrentState();
    };
  };
}
