import { sendInstanceUpdate } from "../../websocket/sendUpdateUtils.ts";
import { prepareDeploymentContext } from "./deploymentUtils.ts";
import { buildAndDeploy } from "./buildProcessHandler.ts";
import { useDeploymentStore } from "../../../stores/deploymentStore.tsx";
import { useDockerStore } from "../../../stores/dockerStore.tsx";

export async function handleDockerBuild(compute: DeploymentCommand) {
  try {
    //기존에 있었으면 rebuild 요청임 삭제하고 재시작
    const deploymentstore = useDeploymentStore.getState();
    const dockerstore = useDockerStore.getState();

    // 기존에 존재하던건지 확인
    const existingContainerId = Object.entries(deploymentstore.containers).find(
      ([_, deployment]) => deployment.deploymentId === compute.deploymentId
    )?.[0];

    if (existingContainerId) {
      //존재하면 container 삭제
      await window.electronAPI.stopContainerStats([existingContainerId]);
      await window.electronAPI.removeContainer(existingContainerId);
      deploymentstore.removeContainer(existingContainerId);
      dockerstore.removeDockerContainer(existingContainerId);
      console.log(
        `Removed existing deployment with ID: ${compute.deploymentId}`
      );
    }

    //compute env, dockerfile 여부 확인하고 생성 후 반환
    const { contextPath, dockerfilePath } = await prepareDeploymentContext(
      compute
    );

    if (!contextPath) {
      return;
    }
    await buildAndDeploy(compute, contextPath, dockerfilePath);
  } catch (error) {
    console.error("Error during Docker build and setup:", error);
  }
}
