package com.parkro.client.domain.payment.data

/**
 * 웹뷰로 정산 데이터 넘겨주기 위한 데이터 클래스
 *
 * @author 김지수
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   김지수      최초 생성
 * </pre>
 */
data class PaymentData(
    val amount: String,
    val orderId: String,
    val username: String,
    val orderName: String,
    val customerName: String,
    val parkingId: String,
    val memberCouponId: String,
    val receiptId: String,
    val couponDiscountTime: String,
    val receiptDiscountTime: String,
    val totalParkingTime: String, // 총 주차 시간 (분 단위)
    val totalPrice: String,
    val card: String,
)

