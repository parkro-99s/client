package com.parkro.client.domain.admin_parkinglist.api

/**
 * 관리자 주차 내역 리스트 DTO
 *
 * @author 김지수
 * @since 2024.08.05
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.05   김지수      최초 생성
 * </pre>
 */
data class GetAdminParkingRes(
    val carNumber: String,
    val entranceDate: String,
    val exitDate: String,
    val memberId: Int,
    val parkingId: Int,
    val parkingLotId: Int,
    val parkingLotName: String,
    val status: String,
    val storeName: String
)
