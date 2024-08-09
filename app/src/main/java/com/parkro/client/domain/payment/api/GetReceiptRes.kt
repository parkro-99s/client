package com.parkro.client.domain.payment.api

import com.google.gson.annotations.SerializedName

/**
 * 영수증 정보 조회 DTO
 *
 * @author 김지수
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   김지수      최초 생성
 * </pre>
 */
data class GetReceiptRes (
    @SerializedName("receiptId")
    val receiptId: Int,

    @SerializedName("storeId")
    val storeId: Int,

    @SerializedName("totalPrice")
    val totalPrice: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdDate")
    val createdDate: String
)