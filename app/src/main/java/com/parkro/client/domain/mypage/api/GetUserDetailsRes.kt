package com.parkro.client.domain.mypage.api

import com.google.gson.annotations.SerializedName

data class GetUserDetailsRes(

    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("carNumber")
    val carNumber: String,

    @SerializedName("carProfile")
    val carProfile: Int
)
