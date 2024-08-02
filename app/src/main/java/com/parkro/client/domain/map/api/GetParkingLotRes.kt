package com.parkro.client.domain.map.api

import com.google.gson.annotations.SerializedName

data class GetParkingLotRes(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("isInternal")
    val isInternal: String,

    @SerializedName("totalSpaces")
    val totalSpaces: Int,

    @SerializedName("usedSpaces")
    val usedSpaces: Int
)
