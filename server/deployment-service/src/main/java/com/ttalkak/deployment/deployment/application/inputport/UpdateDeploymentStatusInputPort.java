package com.ttalkak.deployment.deployment.application.inputport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ttalkak.deployment.common.global.error.ErrorCode;
import com.ttalkak.deployment.common.global.exception.BusinessException;
import com.ttalkak.deployment.common.global.exception.EntityNotFoundException;
import com.ttalkak.deployment.deployment.application.outputport.*;
import com.ttalkak.deployment.deployment.application.usecase.UpdateDeploymentStatusUseCase;
import com.ttalkak.deployment.deployment.domain.event.*;
import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import com.ttalkak.deployment.deployment.domain.model.HostingEntity;
import com.ttalkak.deployment.deployment.domain.model.VersionEntity;
import com.ttalkak.deployment.deployment.domain.model.vo.Status;
import com.ttalkak.deployment.deployment.framework.projectadapter.dto.ProjectInfoResponse;
import com.ttalkak.deployment.deployment.framework.web.request.DeploymentUpdateStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ttalkak.deployment.deployment.domain.model.vo.Status.*;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateDeploymentStatusInputPort implements UpdateDeploymentStatusUseCase {

    private final DeploymentOutputPort deploymentOutputPort;

    private final DomainOutputPort domainOutputPort;

    private final HostingOutputPort hostingOutputPort;

    private final EventOutputPort eventOutputPort;

    private final VersionOutputPort versionOutputPort;

    private final ProjectOutputPort projectOutputPort;

    @Override
    public void updateDeploymentStatus(DeploymentUpdateStatusRequest deploymentUpdateStatusRequest){
        DeploymentEntity deploymentEntity = deploymentOutputPort.findDeployment(Long.valueOf(deploymentUpdateStatusRequest.getDeploymentId()))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTS_DEPLOYMENT));

        String message = deploymentUpdateStatusRequest.getMessage();
        Status status = deploymentUpdateStatusRequest.getStatus();

        // 삭제된 상태인 경우에는 아무것도 하지 않는다.
        if(deploymentEntity.getStatus() == DELETED) return;

        if(status == WAITING) {
            if (message.equals("cloud manipulate")) {
                reAllocateInstance(deploymentEntity);
            }
            deploymentEntity.setStatus(WAITING);
        }

        if(status == ERROR){
            deploymentEntity.setStatus(ERROR);
        }

        if(status == DELETED) {
            deploymentEntity.setStatus(status);
            HostingEntity findHosting = hostingOutputPort.findByProjectIdAndServiceType(deploymentEntity.getProjectId(), deploymentEntity.getServiceType());
            findHosting.delete();
            domainOutputPort.deleteDomainKey(findHosting.getId().toString());
            deploymentEntity.deleteDeployment();
        }

        if(status == STOPPED){
            deploymentEntity.setStatus(status);
        }

        if(status == RUNNING){
            deploymentEntity.setStatus(status);
        }
        deploymentEntity.setStatusMessage(message);
        deploymentOutputPort.save(deploymentEntity);
    }

    private void reAllocateInstance(DeploymentEntity deploymentEntity) {
        VersionEntity versionEntity = versionOutputPort.findLastVersionByDeploymentId(deploymentEntity.getId());


        ProjectInfoResponse projectInfo = projectOutputPort.getProjectInfo(deploymentEntity.getProjectId());
        String expirationDate = projectInfo.getExpirationDate();

        HostingEntity hosting = hostingOutputPort.findByProjectIdAndServiceType(deploymentEntity.getProjectId(), deploymentEntity.getServiceType());
        if(hosting == null){
            throw new BusinessException(ErrorCode.NOT_EXISTS_HOSTING);
        }
        List<EnvEvent> envEvents = deploymentEntity.getEnvs().stream().map(env -> new EnvEvent(env.getKey(), env.getValue())).toList();
        HostingEvent hostingEvent = new HostingEvent(
                deploymentEntity.getId(),
                hosting.getId(),
                hosting.getHostingPort(),
                hosting.getDeployerId(),
                hosting.getDetailSubDomainName(),
                hosting.getDetailSubDomainKey()
        );

        DeploymentEvent deploymentEvent = new DeploymentEvent(deploymentEntity.getId(), deploymentEntity.getProjectId(), envEvents, deploymentEntity.getServiceType().toString());
        GithubInfoEvent githubInfoEvent = new GithubInfoEvent(deploymentEntity.getGithubInfo().getRepositoryUrl(), deploymentEntity.getGithubInfo().getRootDirectory(), deploymentEntity.getGithubInfo().getBranch());
        CreateInstanceEvent createInstanceEvent = null;
        if(deploymentEntity.getDockerfileScript().equals("Docker File Exist")){
            createInstanceEvent = new CreateInstanceEvent(deploymentEvent, hostingEvent, githubInfoEvent, envEvents, versionEntity.getVersion(), expirationDate, true, deploymentEntity.getDockerfileScript());
        }else{
            createInstanceEvent = new CreateInstanceEvent(deploymentEvent, hostingEvent, githubInfoEvent, envEvents, versionEntity.getVersion(), expirationDate, false, deploymentEntity.getDockerfileScript());
        }

        try {
            eventOutputPort.occurRebuildInstance(createInstanceEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카프카 요청 오류가 발생했습니다.");
        }
    }
}
