package com.parkro.client.domain.map.data

import com.parkro.client.domain.map.api.GetParkingLotRes
import com.parkro.client.domain.map.api.ParkingLotService
import com.parkro.client.domain.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 주차장 관련 Repository
 *
 * @author 김민정
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김민정       최초 생성
 * </pre>
 */
class ParkingLotRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ParkingLotService 구현체 전달
    private val parkingLotService: ParkingLotService by lazy {
        RetrofitClient.instance.create(ParkingLotService::class.java)
    }

    // 주차장 목록 조회
    fun getParkingLotList(storeId: String, onResult: (Result<List<GetParkingLotRes>>) -> Unit) {

        // 비동기적으로 API 요청 수행
        parkingLotService.getParkingLotList(storeId).enqueue(object : Callback<List<GetParkingLotRes>> {

            // 응답 호출 성공
            override fun onResponse(call: Call<List<GetParkingLotRes>>, response: Response<List<GetParkingLotRes>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    // 서버 응답 코드가 200이 아닌 경우, 에러 메시지를 포함해서 전달
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onResult(Result.failure(Throwable(errorMessage)))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<List<GetParkingLotRes>>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}