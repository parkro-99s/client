package com.parkro.client.domain.parkinglist.api

/**
 * 주차 내역 상세 DTO
 *
 * @author 김민정
 * @since 2024.08.04
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.04   김민정       최초 생성
 * </pre>
 */
data class GetParkingDetailRes(

    val parkingId: Int,
    val paymentId: Int,
    val parkingLotId: Int,
    val storeId: Int,

    val card: String,
    val carNumber: String,
    val parkingLotName: String,
    val storeName: String,
    val parkingSatus: String,

    val entranceDate: String,       // 입차 완료 시각
    val paymentTime: String,        // 정산 완료 시각
    val exitDate: String,           // 출차 완료 시각

    val parkingTimeHour : Int,      // 주차 시간 - 시
    val parkingTimeMinute : Int,    // 주차 시간 - 분

    val receiptDiscountTime: Int,   // 영수증 할인
    val couponDiscountTime: Int,    // 쿠폰 할인

    val totalParkingTime: Int,      // 정산 시간
    val totalPrice : Int            // 결제 금액
)
