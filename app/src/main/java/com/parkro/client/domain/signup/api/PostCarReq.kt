package com.parkro.client.domain.signup.api

import com.google.gson.annotations.SerializedName

// 차량 조회
data class PostCarReq (
    @SerializedName("REGINUMBER")
    val carNumber: String,

    @SerializedName("OWNERNAME")
    val name: String,
    )
