package com.parkro.client.domain.payment.api

data class PostPaymentReq (
    val amount: String,
    val orderId: String,
    val orderName: String,
    val customerName: String,
    val memberId: String,
    val parkingId: String,
    val memberCouponId: String?,
    val receiptId: String?,
    val couponDiscountTime: String,
    val receiptDiscountTime: String,
    val totalParkingTime: String,
    val totalPrice: String,
    val card: String,
    val paymentDateTime: String,
    val paymentKey: String
)