package com.parkro.client.domain.admin_parkinglist.api

data class GetAdminParkingRes(
    val carNumber: String,
    val entranceDate: String,
    val exitDate: String,
    val memberId: Int,
    val parkingId: Int,
    val parkingLotId: Int,
    val parkingLotName: String,
    val status: String,
    val storeName: String
)
