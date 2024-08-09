package com.parkro.client.domain.payment.api

/**
 * 정산 내역 등록 DTO
 *
 * @author 김지수
 * @since 2024.08.04
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.04   김지수      최초 생성
 * </pre>
 */
data class PostPaymentReq (
    val paymentId: Int? = null, // 추가된 필드
    val username: String? = null, // 추가된 필드
    val parkingId: Int,
    val memberCouponId: Int? = null,
    val receiptId: Int? = null,
    val couponDiscountTime: Int? = null, // 추가된 필드
    val receiptDiscountTime: Int? = null, // 추가된 필드
    val totalParkingTime: Int,
    val totalPrice: Int,
    val card: String,
    val paymentTime: String? = null, // 추가된 필드, Date를 String으로 변환
    val paymentKey: String? = null, // 추가된 필드
    val cancelledDate: String? = null, // 추가된 필드, Date를 String으로 변환
    val status: String? = null, // 추가된 필드
)