package kr.kro.ttalkak.deployment.deployment.adapter.out.persistence.entity;

import lombok.Getter;

@Getter
public enum DeployStatus {
    READY, ERROR, BUILDING, QUEUED, CANCELED
}
