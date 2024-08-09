package com.parkro.client.domain.parkinglist.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query

    /**
     * 주차장
     *
     * @author 김민정
     * @since 2024.08.03
     *
     * <pre>
     * 수정일자       수정자        수정내용
     * ------------ --------    ---------------------------
     * 2024.08.02   김민정       최초 생성
     * </pre>
     */
interface ParkingService {


    /**
     * username(유저아이디)로 주차 내역을 조회하는 메서드
     * @param username
     * @param page
     * @return 주차 내역 리스트
     */
    @GET("/parking/list")
    fun getParkingList(@Query("username") username: String,
                        @Query("page") page:Int) : Call<List<GetParkingRes>>

    /**
     * pakringId로 주차 내역 상세 조회
     * @param parking
     * @return 주차 상세 정보
     */
    @GET("/payment")
    fun getParkingDetail(@Query("parking") parking: Int) : Call<GetParkingDetailRes>

    /**
     * pakringId로 주차 내역 삭제
     * @param parkingId
     * @return 업데이트된 row
     */
    // 주차 내역 삭제
    @DELETE("/parking/{parkingId}")
    fun deleteParkingDetail(@Path("parkingId") parkingId: Int): Call<Int>

}