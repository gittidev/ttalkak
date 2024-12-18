package com.ttalkak.compute.compute.adapter.`in`.socket.request

import com.ttalkak.compute.compute.domain.ComputerType

data class ComputeConnectRequest(
    val userId: Long,
    val computerType: ComputerType,
    val usedCompute: Int,
    val usedMemory: Long,
    val usedCPU: Double,
    val ports: List<Int>,
)
