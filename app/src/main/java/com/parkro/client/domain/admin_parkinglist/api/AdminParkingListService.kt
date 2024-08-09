package com.parkro.client.domain.admin_parkinglist.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminParkingListService {

    /**
     * 주차 내역 핕터링, 페이징 조회
     * @param storeId
     * @param parkingLotId
     * @param date
     * @param car
     * @param page
     * @return 지점, 주차장, 날짜, 차량 번호 필터링 조건 걸고 페이징한 조회 결과 리스트
     */
    @GET("/admin/parking/list")
    fun getAdminParkingList(@Query("storeId") storeId: Int, @Query("parkingLotId") parkingLotId: Int, @Query("date") date: String, @Query("car") car: String? = null, @Query("page") page: Int): Call<List<GetAdminParkingRes>>

    /**
     * 주차 내역 상세 정보 조회
     * @param parkingId
     * @return 주차 상세 정보
     */
    @GET("/admin/parking/detail/{parkingId}")
    fun getAdminParkingListDetail(@Path("parkingId") parkingId: Int): Call<GetAdminParkingDetailRes>

    /**
     * 관리자 단에서 결제 전인 주차 내역에 대해 결제 완료
     * @param parkingId
     * @return 업데이트된 row 수
     */
    @PATCH("/admin/parking/out/{parkingId}")
    fun patchAdminParkingOut(@Path("parkingId") parkingId: Int): Call<Int>
}