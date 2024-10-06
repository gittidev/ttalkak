package com.ttalkak.compute.compute.application.port.out

import com.ttalkak.compute.compute.domain.ComputeRunning
import com.ttalkak.compute.compute.domain.ServiceType

interface LoadRunningPort {
    fun loadRunning(id: Long, serviceType: ServiceType): ComputeRunning
}