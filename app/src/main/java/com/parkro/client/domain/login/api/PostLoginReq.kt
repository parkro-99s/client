package com.parkro.client.domain.login.api

import com.google.gson.annotations.SerializedName

// 로그인
data class PostLoginReq (
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,
)
