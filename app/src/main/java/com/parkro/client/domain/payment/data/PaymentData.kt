package com.parkro.client.domain.payment.data

data class PaymentData(
    val amount: String,
    val orderId: String,
    val orderName: String,
    val customerName: String,
    val memberId: String,
    val parkingId: String,
    val memberCouponId: String,
    val receiptId: String,
    val couponDiscountTime: String,
    val receiptDiscountTime: String,
    val totalParkingTime: String, // 총 주차 시간 (분 단위)
    val totalPrice: String,
    val card: String,
)

