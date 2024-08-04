package com.parkro.client.domain.parkinglist.data

import com.google.gson.Gson
import com.parkro.client.common.data.ErrorRes
import com.parkro.client.common.data.ErrorResponseException
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.parkinglist.api.GetParkingDetailRes
import com.parkro.client.domain.parkinglist.api.GetParkingRes
import com.parkro.client.domain.parkinglist.api.ParkingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkingRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ParkingService 구현체 전달
    private val parkingService: ParkingService by lazy {
        RetrofitClient.instance.create(ParkingService::class.java)
    }

    // 나의 주차 내역 목록 조회
    fun getParkingList(storeId: String, page: Int, onResult: (Result<List<GetParkingRes>>) -> Unit) {

        // 비동기적으로 API 요청 수행
        parkingService.getParkingList(storeId, page).enqueue(object :
            Callback<List<GetParkingRes>> {

            // 응답 호출 성공
            override fun onResponse(call: Call<List<GetParkingRes>>, response: Response<List<GetParkingRes>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBody, ErrorRes::class.java)
                    onResult(Result.failure(ErrorResponseException(errorRes)))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<List<GetParkingRes>>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    // 주차 내역 상세 조회
    fun getParkingDetail(parkingId: Int, onResult: (Result<GetParkingDetailRes>) -> Unit) {
        parkingService.getParkingDetail(parkingId).enqueue(object : Callback<GetParkingDetailRes> {

            // 응답 호출 성공
            override fun onResponse(call: Call<GetParkingDetailRes>, response: Response<GetParkingDetailRes>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBody, ErrorRes::class.java)
                    onResult(Result.failure(ErrorResponseException(errorRes)))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<GetParkingDetailRes>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    // 주차 내역 삭제
    fun deleteParkingDetail(parkingId: Int, onResult: (Result<Int>) -> Unit) {
        parkingService.deleteParkingDetail(parkingId).enqueue(object : Callback<Int> {

            // 응답 호출 성공
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBody, ErrorRes::class.java)
                    onResult(Result.failure(ErrorResponseException(errorRes)))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<Int>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}