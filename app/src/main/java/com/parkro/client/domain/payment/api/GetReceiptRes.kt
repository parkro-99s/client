package com.parkro.client.domain.payment.api

import com.google.gson.annotations.SerializedName

data class GetReceiptRes (
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