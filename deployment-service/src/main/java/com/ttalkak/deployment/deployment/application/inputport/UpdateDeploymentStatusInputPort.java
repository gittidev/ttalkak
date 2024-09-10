package com.ttalkak.deployment.deployment.application.inputport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ttalkak.deployment.deployment.application.outputport.DeploymentOutputPort;
import com.ttalkak.deployment.deployment.application.usecase.UpdateDeploymentStatusUsecase;
import com.ttalkak.deployment.deployment.domain.event.UpdateDeploymentStatusEvent;
import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import com.ttalkak.deployment.deployment.domain.model.vo.DeploymentStatus;
import com.ttalkak.deployment.deployment.framework.kafka.ChangeStatusProducer;
import com.ttalkak.deployment.deployment.framework.web.request.DeploymentUpdateStatusRequest;
import com.ttalkak.deployment.global.error.ErrorCode;
import com.ttalkak.deployment.global.exception.BusinessException;
import com.ttalkak.deployment.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateDeploymentStatusInputPort implements UpdateDeploymentStatusUsecase {

    private final DeploymentOutputPort deploymentOutputPort;

    @Override
    public void updateDeploymentStatus(DeploymentUpdateStatusRequest deploymentUpdateStatusRequest){
        DeploymentEntity deploymentEntity = deploymentOutputPort.findDeployment(Long.valueOf(deploymentUpdateStatusRequest.getDeploymentId()))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTS_DEPLOYMENT));

        deploymentEntity.setStatus(DeploymentStatus.valueOf(deploymentUpdateStatusRequest.getStatus()));
        deploymentOutputPort.save(deploymentEntity);
    }
}
