package com.ttalkak.deployment.deployment.framework.jpaadapter.adapter;

import com.ttalkak.deployment.deployment.application.outputport.HostingOutputPort;
import com.ttalkak.deployment.deployment.domain.model.HostingEntity;
import com.ttalkak.deployment.deployment.framework.jpaadapter.repository.HostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HostingAdapter implements HostingOutputPort {

    private final HostingRepository hostingRepository;

    @Override
    public HostingEntity save(HostingEntity hostingEntity) {
        return hostingRepository.save(hostingEntity);
    }

    @Override
    public Optional<HostingEntity> findById(Long hostingId) {
        return hostingRepository.findById(hostingId);
    }

    @Override
    public HostingEntity findByProjectIdAndServiceType(Long projectId, String serviceType) {
        // TODO: 에러 처리 추가
        return hostingRepository.findByProjectIdAndServiceType(projectId, serviceType)
                .orElseThrow(() -> new RuntimeException("HostingEntity not found"));
    }
}
