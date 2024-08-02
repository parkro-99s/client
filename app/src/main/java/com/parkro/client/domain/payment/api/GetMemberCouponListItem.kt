package com.parkro.client.domain.payment.api

data class GetMemberCouponListItem(
    val couponId: Int,
    val createdDate: String,
    val discountHour: Int,
    val endDate: String,
    val memberCouponId: Int,
    val parkingLotId: Any,
    val status: String
)