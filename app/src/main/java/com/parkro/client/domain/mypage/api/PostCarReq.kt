package com.parkro.client.domain.mypage.api
import com.google.gson.annotations.SerializedName
/**
 * 차량 등록 클래스
 *
 * @author 양재혁
 * @since 2024.08.04
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.04   양재혁      최초 생성
 * </pre>
 */

data class PostCarReq (
    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("carNumber")
    val carNumber: String,
)
