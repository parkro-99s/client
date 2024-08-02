package com.parkro.client.domain.map.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingLotService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/parking-lot")
    fun getParkingLotList(@Query("store") storeId: String): Call<List<GetParkingLotRes>>
}