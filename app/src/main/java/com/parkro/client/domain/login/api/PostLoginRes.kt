package com.parkro.client.domain.login.api

import com.google.gson.annotations.SerializedName

// 로그인
data class PostLoginRes(
    @SerializedName("token")
    val token: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("carProfile")
    val carProfile: Int?,
)
