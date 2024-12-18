import { sendInstanceUpdate } from "../../../services/websocket/sendUpdateUtils";
import { PGROK_URL } from "../pgrokHandler";
import { useContainerStore } from "../../../stores/containerStore";
import { useImageStore } from "../../../stores/imageStore";
import { DeployImageInfo } from "../../../stores/imageStore";
import { DeployContainerInfo } from "../../../stores/containerStore";
import { DeployStatus } from "../../../types/deploy";
import { checkAndUpdateContainerMonitoring } from "../../../services/monitoring/healthCheckPingUtils";
import { stopAndRemoveExistingContainer } from "./deploymentUtils";
import { useAuthStore } from "../../../stores/authStore";
import { startPostInterval } from "../../../axios/payment.tsx";

export async function handleDatabaseBuild(dbCreate: DatabaseCreateEvent) {
  const { senderId, instance } = dbCreate;
  const { getContainerById, updateContainerInfo } =
    useContainerStore.getState();
  const { updateImageInfo } = useImageStore.getState();
  const id = `${instance.serviceType}-${instance.databaseId}`;
  const dbImageName = `${instance.dockerImageName}:${
    instance.dockerImageTag || "latest"
  }`;

  try {
    // 1. 기존 컨테이너가 있는지 확인하고 삭제 처리
    const existingContainer = getContainerById(id);

    if (existingContainer) {
      const existingContainerId = existingContainer.containerId;
      if (existingContainerId) {
        try {
          await stopAndRemoveExistingContainer(existingContainerId);
          updateImageInfo(id, { status: DeployStatus.DELETED });
          updateContainerInfo(id, { status: DeployStatus.DELETED });
          console.log(`기존 컨테이너 삭제 완료, ID: ${existingContainerId}`);
        } catch (error) {
          console.error(`기존 배포 삭제 중 오류 발생: ${error}`);
          throw error;
        }
        return existingContainerId;
      }
    }

    // 2. 새로운 Docker 컨테이너 생성 및 실행
    const { success, image, container, error } =
      await window.electronAPI.pullAndStartDatabaseContainer(
        instance.dockerImageName,
        dbImageName,
        instance.containerName,
        instance.inboundPort,
        instance.outboundPort,
        instance.envs
      );

    if (success && container?.Id) {
      handleSuccessfulContainerStart(instance, image, container, senderId, id);
    } else if (error) {
      console.log(error);
    }
  } catch (error) {
    console.error("Error during database build process:", error);
    updateImageInfo(id, { status: DeployStatus.ERROR });
    updateContainerInfo(id, { status: DeployStatus.ERROR });
  }
}

// 새로운 컨테이너 시작 성공 시 처리 함수
async function handleSuccessfulContainerStart(
  instance: DatabaseCommand,
  image: DockerImage,
  container: DockerContainer,
  senderId: number,
  id: string
) {
  const { updateImageInfo } = useImageStore.getState();
  const { updateContainerInfo } = useContainerStore.getState();
  const address = useAuthStore.getState().userSettings?.address;
  // 이미지 정보를 업데이트
  const newImage: Omit<DeployImageInfo, "id"> = {
    imageId: image.Id,
    serviceType: instance.serviceType,
    deployId: instance.databaseId,
    RepoTags: image.RepoTags,
    Created: image.Created,
    Size: image.Size,
    Containers: image.Containers,
    status: DeployStatus.RUNNING,
  };
  //container 정보 업데이트
  const newContainer: Omit<DeployContainerInfo, "id"> = {
    senderId: senderId,
    deployId: instance.databaseId,
    serviceType: instance.serviceType,
    containerName: `${instance.dockerImageName}:${instance.dockerImageTag}`,
    imageTag: image.RepoTags ? image.RepoTags[0] : undefined,
    status: DeployStatus.RUNNING,
    containerId: container.Id,
    ports: [
      {
        internal: instance.inboundPort,
        external: instance.outboundPort,
      },
    ],
    created: container.Created,
  };
  updateImageInfo(id, newImage);
  // 상태 업데이트
  // pgrok 실행 및 결과 처리
  const result = await window.electronAPI.runPgrok(
    PGROK_URL,
    `localhost:${instance.outboundPort}`,
    instance.subdomainKey,
    instance.databaseId
  );

  switch (result) {
    case "FAILED":
      sendInstanceUpdate(
        instance.serviceType,
        instance.databaseId,
        senderId,
        "ERROR",
        instance.outboundPort,
        "FAILED"
      );
      break;
    case "SUCCESS":
      sendInstanceUpdate(
        instance.serviceType,
        instance.databaseId,
        senderId,
        "RUNNING",
        instance.outboundPort,
        "RUNNING"
      );

      //pgrok 생성 되고 나서
      updateContainerInfo(id, newContainer);
      checkAndUpdateContainerMonitoring();
      // window.electronAPI.startLogStream(container.Id);

      if (newContainer && address) {
        const paymentContainer = {
          id: id,
          domain: "database", // 도메인 정보
          deployId: instance.databaseId, // 배포 ID
          serviceType: instance.serviceType, // 서비스 타입
          senderId: senderId, // 발신자 ID
          address: address, // 주소 정보
        };

        startPostInterval(paymentContainer);
      }
      break;
    default:
      break;
  }
}
