package com.parkro.client.domain.map.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 주차장
 *
 * @author 김민정
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김민정       최초 생성
 * 2204.08.02   김민정       지점별 주차장 목록 조회
 * </pre>
 */
interface ParkingLotService {

    /**
     * 지점별 주차장 목록 조회 메서드
     * @param storeId
     * @return List<GetParkingLotRes>
     */
    @GET("/parking-lot")
    fun getParkingLotList(@Query("store") storeId: String): Call<List<GetParkingLotRes>>
}