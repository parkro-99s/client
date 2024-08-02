package com.parkro.client.domain.mypage.api

import com.google.gson.annotations.SerializedName

data class GetUserDetailsRes(

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("carNumber")
    val carNumber: String
)
