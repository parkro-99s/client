package com.parkro.client.domain.login.api

import com.google.gson.annotations.SerializedName
/**
 * 로그인 클래스
 *
 * @author 양재혁
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   양재혁      최초 생성
 * </pre>
 */
data class PostLoginReq (
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,
)
