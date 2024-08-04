package com.parkro.client.domain.mypage.api
import com.google.gson.annotations.SerializedName

// 로그인
data class PostCarReq (
    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("carNumber")
    val carNumber: String,
)
