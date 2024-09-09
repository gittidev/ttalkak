package com.ttalkak.deployment.deployment.application.inputport;

import com.ttalkak.deployment.deployment.application.outputport.DeploymentOutputPort;
import com.ttalkak.deployment.deployment.application.usecase.DeleteDeploymentUsecase;
import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import com.ttalkak.deployment.deployment.framework.web.request.DeploymentDeleteRequest;
import com.ttalkak.deployment.global.error.ErrorCode;
import com.ttalkak.deployment.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class DeleteDeploymentInputPort implements DeleteDeploymentUsecase {

    private final DeploymentOutputPort deploymentOutputPort;

    @Override
    public void deleteDeployment(DeploymentDeleteRequest deploymentDeleteRequest) {
        DeploymentEntity deploymentEntity = deploymentOutputPort.findDeployment(deploymentDeleteRequest.getDeploymentId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTS_DEPLOYMENT));
        deploymentEntity.deleteDeployment();
        deploymentOutputPort.save(deploymentEntity);
    }

    @Override
    public void deleteDeploymentByProject(Long projectId) throws Exception {
        List<DeploymentEntity> deploymentEntities = deploymentOutputPort.findAllByProjectId(projectId);
        for(DeploymentEntity deploymentEntity : deploymentEntities) {
            deploymentEntity.deleteDeployment();
        }
        deploymentOutputPort.saveAll(deploymentEntities);

    }
}
