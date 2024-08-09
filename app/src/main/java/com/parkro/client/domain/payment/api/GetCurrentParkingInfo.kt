package com.parkro.client.domain.payment.api

/**
 * 현재 주차 중인 주차 정보 조회 DTO
 *
 * @author 김지수
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   김지수      최초 생성
 * </pre>
 */
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
