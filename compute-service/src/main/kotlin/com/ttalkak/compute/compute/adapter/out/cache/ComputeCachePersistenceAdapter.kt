package com.ttalkak.compute.compute.adapter.out.cache

import com.ttalkak.compute.common.PersistenceAdapter
import com.ttalkak.compute.compute.adapter.out.cache.entity.ComputeUserCache
import com.ttalkak.compute.compute.adapter.out.cache.repository.ComputeUserCacheRepository
import com.ttalkak.compute.compute.application.port.out.SaveComputePort
import com.ttalkak.compute.compute.domain.ComputerType

@PersistenceAdapter
class ComputeCachePersistenceAdapter(
    private val computeUserCacheRepository: ComputeUserCacheRepository
): SaveComputePort {
    override fun saveCompute(
        userId: Long,
        computeType: ComputerType,
        maxMemory: Int
    ) {
        val compute = ComputeUserCache(
            userId = userId,
            usedCompute = 0,
            computeType = computeType,
            maxMemory = maxMemory
        )

        computeUserCacheRepository.save(compute)
    }

    override fun deleteCompute(userId: Long) {
        computeUserCacheRepository.delete(userId)
    }
}