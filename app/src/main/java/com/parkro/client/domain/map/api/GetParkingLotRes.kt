package com.parkro.client.domain.map.api

import com.google.gson.annotations.SerializedName

/**
 * 주차장 DTO
 *
 * @author 김민정
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김민정       최초 생성
 * </pre>
 */
data class GetParkingLotRes(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("isInternal")
    val isInternal: String,

    @SerializedName("totalSpaces")
    val totalSpaces: Int,

    @SerializedName("usedSpaces")
    val usedSpaces: Int
)
