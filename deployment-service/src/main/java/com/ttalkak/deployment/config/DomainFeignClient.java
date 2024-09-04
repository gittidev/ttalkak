package com.ttalkak.deployment.config;

import com.ttalkak.deployment.deployment.framework.domainadapter.dto.DomainKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "DOMAIN-SERVICE")
public interface DomainFeignClient {

    @PostMapping("/v1/domain/{hostingId}")
    public DomainKeyResponse getDomainKey(@PathVariable Long hostingId);
}
