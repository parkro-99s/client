package com.parkro.client.domain.payment.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/payment")
    fun getParkingInfo(@Query("parking") parkingId: Int): Call<GetCurrentParkingInfo>

    @GET("/parking")
    fun getParkingInfoFirst(@Query("username") username: String): Call<GetCurrentParkingInfo>

    @GET("/payment/coupon/{username}")
    fun getMemberCouponList(@Path("username") username: String): Call<GetMemberCouponList>
}