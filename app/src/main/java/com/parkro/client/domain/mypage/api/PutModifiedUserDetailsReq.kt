package com.parkro.client.domain.mypage.api

import com.google.gson.annotations.SerializedName

data class PutModifiedUserDetailsReq(

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("carProfile")
    val carProfile: Int
)
