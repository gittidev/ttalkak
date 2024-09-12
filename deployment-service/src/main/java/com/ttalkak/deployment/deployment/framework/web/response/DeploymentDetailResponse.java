package com.ttalkak.deployment.deployment.framework.web.response;

import com.ttalkak.deployment.deployment.domain.model.DeploymentEntity;
import com.ttalkak.deployment.deployment.domain.model.HostingEntity;
import com.ttalkak.deployment.deployment.domain.model.vo.DeploymentStatus;
import com.ttalkak.deployment.deployment.domain.model.vo.ServiceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DeploymentDetailResponse {

    private Long deploymentId;

    private Long projectId;

    @Enumerated(EnumType.STRING)
    private DeploymentStatus status;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    private String repositoryName;

    private String repositoryUrl;

    private String branch;

    private String repositoryLastCommitMessage;

    private String repositoryLastCommitUserProfile;

    private String repositoryLastCommitUserName;

    private String framework;

    private List<EnvResponse> envs;

    private HostingResponse hostingResponse;

    private List<DatabaseResponse> databaseResponses;

    @Builder
    private DeploymentDetailResponse(Long deploymentId, Long projectId, DeploymentStatus status, ServiceType serviceType, String repositoryName, String repositoryUrl, String repositoryLastCommitMessage, String repositoryLastCommitUserProfile, String repositoryLastCommitUserName, HostingResponse hostingResponse, List<EnvResponse> envs, String branch, String framework, List<DatabaseResponse> databaseResponses) {
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.status = status;
        this.serviceType = serviceType;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.repositoryLastCommitMessage = repositoryLastCommitMessage;
        this.repositoryLastCommitUserProfile = repositoryLastCommitUserProfile;
        this.repositoryLastCommitUserName = repositoryLastCommitUserName;
        this.hostingResponse = hostingResponse;
        this.branch = branch;
        this.envs = envs;
        this.framework = framework;
        this.databaseResponses = databaseResponses;
    }

    public static DeploymentDetailResponse mapToDTO(DeploymentEntity deploymentEntity, HostingEntity hostingEntity){
        return DeploymentDetailResponse.builder()
                .deploymentId(deploymentEntity.getId())
                .projectId(deploymentEntity.getProjectId())
                .status(deploymentEntity.getStatus())
                .serviceType(deploymentEntity.getServiceType())
                .repositoryUrl(deploymentEntity.getGithubInfo().getRepositoryUrl())
                .repositoryName(deploymentEntity.getGithubInfo().getRepositoryName())
                .hostingResponse(HostingResponse.mapToDTO(hostingEntity))
                .envs(deploymentEntity.getEnvs().stream()
                        .map(EnvResponse::mapToDTO)
                        .toList())
                .branch(deploymentEntity.getGithubInfo().getBranch())
                .framework(deploymentEntity.getFramework())
                .databaseResponses(deploymentEntity.getDataBaseEntities().stream()
                        .map(DatabaseResponse::mapToDTO)
                        .toList())
                .build();
    }
}
