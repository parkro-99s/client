package com.parkro.client.domain.admin_parkinglist.api

/**
 * 관리자 주차 내역 상세 정보 DTO
 *
 * @author 김지수
 * @since 2024.08.05
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.05   김지수      최초 생성
 * </pre>
 */
data class GetAdminParkingDetailRes(
    val carNumber: String,
    val couponDiscountTime: Int,
    val entranceDate: String,
    val exitDate: String,
    val parkingLotName: String,
    val parkingStatus: String,
    val parkingTimeHour: Int,
    val parkingTimeMinute: Int,
    val paymentDate: String,
    val receiptDiscountTime: Int,
    val storeName: String,
    val totalParkingTime: Int,
    val totalPrice: Int
)