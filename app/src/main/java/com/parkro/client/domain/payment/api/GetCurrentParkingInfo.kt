package com.parkro.client.domain.payment.api

// 현재 사용자의 주차 정보 조회
data class GetCurrentParkingInfo (
    val carNumber: String,
    val parkingId: Int,
    val status: String,
    val storeName: String,
    val parkingLotName: String,
    val entranceDate: String,
    val parkingTimeHour: Int? = null,
    val parkingTimeMinute: Int? = null,
    val perPrice: Int? = null
)
