package com.ttalkak.deployment.deployment.framework.web.request;

import com.ttalkak.deployment.deployment.domain.model.vo.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class DeploymentUpdateStatusRequest {

    private Long deploymentId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String message;
}
