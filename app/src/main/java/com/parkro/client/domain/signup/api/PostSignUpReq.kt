package com.parkro.client.domain.signup.api

import com.google.gson.annotations.SerializedName
/**
 * 회원가입 클래스
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   양재혁      최초 생성
 * </pre>
 */
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
