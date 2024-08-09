package com.parkro.client.domain.mypage.api

import com.google.gson.annotations.SerializedName
/**
 * 회원 정보 수정 클래스
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.04   양재혁      최초 생성
 * </pre>
 */
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
