package com.parkro.client.domain.payment.api

/**
 * 현재 사용자가 가진 쿠폰 목록 조회 DTO
 *
 * @author 김지수
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   김지수      최초 생성
 * </pre>
 */
class GetMemberCouponList : ArrayList<GetMemberCouponListItem>()