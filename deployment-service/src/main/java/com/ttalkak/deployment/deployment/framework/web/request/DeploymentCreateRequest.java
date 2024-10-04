package com.ttalkak.deployment.deployment.framework.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class DeploymentCreateRequest {

    @NotNull(message = "프로젝트 아이디는 필수입니다.")
    private Long projectId;

    @NotNull(message = "서비스타입은 필수입니다.")
    private String serviceType;

    @NotNull(message = "호스팅 포트는 필수입니다.")
    private int hostingPort;

    private GithubRepositoryRequest githubRepositoryRequest;

    private VersionRequest versionRequest;

    private List<EnvCreateRequest> envs = new ArrayList<>();

    private String framework;

    @Nullable
    private DockerfileCreateRequest dockerfileCreateRequest;

    @Builder
    private DeploymentCreateRequest(Long projectId, String serviceType, GithubRepositoryRequest githubRepositoryRequest, VersionRequest versionRequest, int hostingPort, List<EnvCreateRequest> envs, String framework, DockerfileCreateRequest dockerfileCreateRequest) {
        this.projectId = projectId;
        this.serviceType = serviceType;
        this.githubRepositoryRequest = githubRepositoryRequest;
        this.versionRequest = versionRequest;
        this.hostingPort = hostingPort;
        this.envs = envs;
        this.framework = framework;
        this.dockerfileCreateRequest = dockerfileCreateRequest;
    }
}
