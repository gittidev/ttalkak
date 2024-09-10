package com.ttalkak.deployment.deployment.framework.web.response;

import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DeploymentResponse {

    private Long deploymentId;

    private Long projectId;

    private String status;

    private String serviceType;

    private String repositoryName;

    private String repositoryUrl;

    private String repositoryLastCommitMessage;

    private String repositoryLastCommitUserProfile;

    private String repositoryLastCommitUserName;

    private String branch;

    private String framework;

    private List<HostingResponse> hostingResponses;

    @Builder
    private DeploymentResponse(Long deploymentId, Long projectId, String status, String serviceType, String repositoryName, String repositoryUrl, String repositoryLastCommitMessage, String repositoryLastCommitUserProfile, String repositoryLastCommitUserName, List<HostingResponse> hostingResponses, String branch, String framework) {
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.status = status;
        this.serviceType = serviceType;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.repositoryLastCommitMessage = repositoryLastCommitMessage;
        this.repositoryLastCommitUserProfile = repositoryLastCommitUserProfile;
        this.repositoryLastCommitUserName = repositoryLastCommitUserName;
        this.hostingResponses = hostingResponses;
        this.branch = branch;
        this.framework = framework;
    }

    public static DeploymentResponse mapToDTO(DeploymentEntity deploymentEntity){
        return DeploymentResponse.builder()
                .deploymentId(deploymentEntity.getId())
                .projectId(deploymentEntity.getProjectId())
                .status(String.valueOf(deploymentEntity.getStatus()))
                .serviceType(String.valueOf(deploymentEntity.getServiceType()))
                .repositoryLastCommitMessage(deploymentEntity.getGithubInfo().getRepositoryLastCommitMessage())
                .repositoryLastCommitUserName(deploymentEntity.getGithubInfo().getRepositoryLastCommitUserName())
                .repositoryLastCommitUserProfile(deploymentEntity.getGithubInfo().getRepositoryLastCommitUserProfile())
                .repositoryUrl(deploymentEntity.getGithubInfo().getRepositoryUrl())
                .repositoryName(deploymentEntity.getGithubInfo().getRepositoryName())
                .hostingResponses(deploymentEntity.getHostingEntities().stream()
                        .map(HostingResponse::mapToDTO)
                        .toList())
                .branch(deploymentEntity.getGithubInfo().getBranch())
                .framework(deploymentEntity.getFramework())
                .build();
    }
}
