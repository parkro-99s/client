package com.parkro.client.domain.payment.api

import retrofit2.Call
import retrofit2.http.*

interface PaymentService {

    /**
     * 현재 주차중인 주차 상세 정보 조회 가져오기
     * @param username
     * @return 사용자(username)가 현재 주차 중인 주차 내역 조회
     */
    @GET("/parking")
    fun getParkingInfoFirst(@Query("username") username: String): Call<GetCurrentParkingInfo>

    /**
     * 사용자가 가진 사용 가능한 상태의 쿠폰 목록 조회
     * @param username
     * @return 사용자(username)가 현재 주차 중인
     */
    @GET("/payment/coupon/{username}")
    fun getMemberCouponList(@Path("username") username: String): Call<GetMemberCouponList>

    /**
     * 결제 내역 등록
     * @param paymentRequest
     * @return 성공 여부
     */
    @POST("/payment")
    fun addPayment(@Body paymentRequest: PostPaymentReq): Call<Int>
}