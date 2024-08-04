package com.parkro.client.domain.parkinglist.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ParkingService {

    // 나의 주차 내역 목록 조회
    @GET("/parking/list")
    fun getParkingList(@Query("username") username: String,
                        @Query("page") page:Int) : Call<List<GetParkingRes>>

    // 주차 내역 상세 조회
    @GET("/payment")
    fun getParkingDetail(@Query("parking") parking: Int) : Call<GetParkingDetailRes>

    // 주차 내역 삭제
    @DELETE("/parking/{parkingId}")
    fun deleteParkingDetail(@Path("parkingId") parkingId: Int): Call<Int>
}