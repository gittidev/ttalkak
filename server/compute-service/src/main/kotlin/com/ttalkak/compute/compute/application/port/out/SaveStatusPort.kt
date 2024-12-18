package com.ttalkak.compute.compute.application.port.out

interface SaveStatusPort {
    /**
     * 컴퓨터 리소스 사용을 저장
     *
     * @param userId 사용자 아이디
     * @param maxCompute 최대 컴퓨터 사용량
     * @param availablePortStart 사용 가능한 포트 시작
     * @param availablePortEnd 사용 가능한 포트 끝
     */
    fun saveStatus(
        userId: Long,
        maxCompute: Int,
        maxCPU: Double,
        maxMemory: Int,
        availablePortStart: Int,
        availablePortEnd: Int
    )

    /**
     * 사용자 주소를 저장
     *
     * @param userId 사용자 아이디
     * @param address 사용자 주소
     */
    fun saveAddress(
        userId: Long,
        address: String?
    )
}