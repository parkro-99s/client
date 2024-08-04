package com.parkro.client.domain.parkinglist.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/parking/list")
    fun getParkingList(@Query("username") username: String,
                        @Query("page") page:Int) : Call<List<GetParkingRes>>
}