package com.parkro.client.domain.signup.api

import com.google.gson.annotations.SerializedName

// 회원 가입
data class PostSignUpReq (
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("carNumber")
    val carNumber: String?,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String
)
