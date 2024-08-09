package com.parkro.client.domain.signup.api

import com.google.gson.annotations.SerializedName
/**
 * 차량 정보 조회 클래스
 *
 * @author 양재혁
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   양재혁      최초 생성
 * </pre>
 */
data class PostCarReq (
    @SerializedName("REGINUMBER")
    val carNumber: String,

    @SerializedName("OWNERNAME")
    val name: String,
    )
