package com.parkro.client.domain.admin_parkinglist.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminParkingListService {

    @GET("/admin/parking/list")
    fun getAdminParkingList(@Query("storeId") storeId: Int, @Query("parkingLotId") parkingLotId: Int, @Query("date") date: String, @Query("car") car: String? = null, @Query("page") page: Int): Call<List<GetAdminParkingRes>>

    @GET("/admin/parking/detail/{parkingId}")
    fun getAdminParkingListDetail(@Path("parkingId") parkingId: Int): Call<GetAdminParkingDetailRes>

    @PATCH("/admin/parking/out/{parkingId}")
    fun patchAdminParkingOut(@Path("parkingId") parkingId: Int): Call<Int>
}