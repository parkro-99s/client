package com.parkro.client.domain.payment.api

/**
 * 현재 사용자가 가진 쿠폰의 상세 조회 DTO
 *
 * @author 김지수
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김지수      최초 생성
 * </pre>
 */
data class GetMemberCouponListItem(
    val couponId: Int,
    val createdDate: String,
    val discountHour: Int,
    val endDate: String,
    val memberCouponId: Int,
    val parkingLotId: Any,
    val status: String
)