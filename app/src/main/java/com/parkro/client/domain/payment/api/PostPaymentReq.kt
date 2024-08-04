package com.parkro.client.domain.payment.api

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