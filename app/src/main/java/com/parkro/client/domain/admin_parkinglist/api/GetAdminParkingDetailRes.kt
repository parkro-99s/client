package com.parkro.client.domain.admin_parkinglist.api

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