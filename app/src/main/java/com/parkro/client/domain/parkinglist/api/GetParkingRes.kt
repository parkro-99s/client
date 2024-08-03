package com.parkro.client.domain.parkinglist.api

data class GetParkingRes(

    val parkingId: Int,
    val storeName: String,
    val parkingLotName: String,
    val carNumber: String,
    val entranceDate: String,
    val status: String
)