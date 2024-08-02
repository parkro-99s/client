package com.parkro.client.domain.payment.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentService {

    @GET("/parking")
    fun findParkingInfoFirst(@Query("username") username: String): Call<GetCurrentParkingInfo>

    @GET("/payment/coupon/{username}")
    fun getMemberCouponList(@Path("username") username: String): Call<GetMemberCouponList>
}