package com.parkro.client.domain.login.api

import com.google.gson.annotations.SerializedName
/**
 * 로그인 성공 시 전달되는 응답에 담을 데이터 클래스
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   양재혁      최초 생성
 * </pre>
 */
// 로그인
data class PostLoginRes(
    @SerializedName("token")
    val token: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("carProfile")
    val carProfile: Int?,
)
