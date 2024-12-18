package com.ttalkak.compute.compute.application.port.`in`

import com.ttalkak.compute.compute.domain.AllocateCompute

interface AllocateUseCase {
    fun addQueue(command: AddComputeCommand)
    fun addRebuildQueue(command: AddComputeCommand)
}