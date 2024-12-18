package com.ttalkak.deployment.deployment.framework.web.response;

import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class DeploymentPreviewResponse {

    private Long deploymentId;

    private Long projectId;

    private String status;

    private String serviceType;

    private String repositoryName;

    private String repositoryUrl;

    private String repositoryLastCommitMessage;

    private String repositoryLastCommitUserProfile;

    private String repositoryLastCommitUserName;

    private String repositoryOwner;

    private String statusMessage;
    @Builder
    private DeploymentPreviewResponse(Long deploymentId,
                                      Long projectId,
                                      String status,
                                      String serviceType,
                                      String repositoryName,
                                      String repositoryUrl,
                                      String repositoryLastCommitMessage,
                                      String repositoryLastCommitUserProfile,
                                      String repositoryLastCommitUserName,
                                      String repositoryOwner,
                                      String statusMessage) {
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.status = status;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.serviceType = serviceType;
        this.repositoryLastCommitMessage = repositoryLastCommitMessage;
        this.repositoryLastCommitUserProfile = repositoryLastCommitUserProfile;
        this.repositoryLastCommitUserName = repositoryLastCommitUserName;
        this.repositoryOwner = repositoryOwner;
        this.statusMessage = statusMessage;
    }

    public static DeploymentPreviewResponse mapToDTO(DeploymentEntity deploymentEntity){
        return DeploymentPreviewResponse.builder()
                .deploymentId(deploymentEntity.getId())
                .projectId(deploymentEntity.getProjectId())
                .status(deploymentEntity.getStatus().toString())
                .repositoryName(deploymentEntity.getGithubInfo().getRepositoryName())
                .repositoryUrl(deploymentEntity.getGithubInfo().getRepositoryUrl())
                .repositoryLastCommitMessage(deploymentEntity.getLastVersion().getRepositoryLastCommitMessage())
                .repositoryLastCommitUserName(deploymentEntity.getLastVersion().getRepositoryLastCommitUserName())
                .repositoryLastCommitMessage(deploymentEntity.getLastVersion().getRepositoryLastCommitMessage())
                .repositoryLastCommitUserProfile(deploymentEntity.getLastVersion().getRepositoryLastCommitUserProfile())
                .repositoryOwner(deploymentEntity.getGithubInfo().getRepositoryOwner())
                .serviceType(deploymentEntity.getServiceType().toString())
                .statusMessage(deploymentEntity.getStatusMessage())
                .build();
    }
}
