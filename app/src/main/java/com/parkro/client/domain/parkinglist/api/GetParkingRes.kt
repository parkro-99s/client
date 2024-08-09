package com.parkro.client.domain.parkinglist.api

/**
 * 주차 내역 DTO
 *
 * @author 김민정
 * @since 2024.08.03
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.03   김민정       최초 생성
 * </pre>
 */
data class GetParkingRes(

    val parkingId: Int,
    val storeName: String,
    val parkingLotName: String,
    val carNumber: String,
    val entranceDate: String,
    val status: String
)