package com.parkro.client.domain.mypage.api

import com.google.gson.annotations.SerializedName
/**
 * 회원 정보 클래스
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.03   양재혁      최초 생성
 * </pre>
 */
data class GetUserDetailsRes(

    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("carNumber")
    val carNumber: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("carProfile")
    val carProfile: Int
)
