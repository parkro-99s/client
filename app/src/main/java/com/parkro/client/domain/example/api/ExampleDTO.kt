package com.parkro.client.domain.example.api

import com.google.gson.annotations.SerializedName

// 사용자 영수증 조회
data class ExampleDTO (
    @SerializedName("receiptId")
    val receiptId: Int,

    @SerializedName("storeId")
    val storeId: Int,

    @SerializedName("totalPrice")
    val totalPrice: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdDate")
    val createdDate: String
)
