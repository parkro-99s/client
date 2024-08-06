package com.parkro.client.domain.admin_parkinglist.data

import android.util.Log
import com.parkro.client.domain.admin_parkinglist.api.AdminParkingListService
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingDetailRes
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingRes
import com.parkro.client.domain.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminParkingListRepository {

    private val adminParkingListService: AdminParkingListService by lazy {
        RetrofitClient.instance.create(AdminParkingListService::class.java)
    }

    fun getAdminParkingList(
        storeId: Int,
        parkingLotId: Int,
        date: String,
        car: String? = null,
        page: Int,
        onResult: (Result<List<GetAdminParkingRes>>) -> Unit
    ) {
        Log.d("AdminParkingListRepository", "[관리자] 주차 목록 요청: $storeId, $date, $car, $page")

        adminParkingListService.getAdminParkingList(storeId, parkingLotId, date, car, page).enqueue(object : Callback<List<GetAdminParkingRes>> {
            override fun onResponse(
                call: Call<List<GetAdminParkingRes>>,
                response: Response<List<GetAdminParkingRes>>
            ) {
                Log.d("AdminParkingListRepository", "[관리자] 주차 정보: $response")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("AdminParkingListRepository", "[관리자] 주차 정보: $responseBody")
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    Log.e("AdminParkingListRepository", "실패: ${response.message()}")
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<GetAdminParkingRes>>, t: Throwable) {
                Log.e("AdminParkingListRepository", "응답 실패: ${t.message}")
                onResult(Result.failure(t))
            }
        })
    }

    fun getAdminParkingListDetail(
        parkingId: Int,
        onResult: (Result<GetAdminParkingDetailRes>) -> Unit
    ) {
        Log.d("AdminParkingListRepository", "[관리자] 주차 상세 정보 요청: $parkingId")

        adminParkingListService.getAdminParkingListDetail(parkingId).enqueue(object : Callback<GetAdminParkingDetailRes> {
            override fun onResponse(
                call: Call<GetAdminParkingDetailRes>,
                response: Response<GetAdminParkingDetailRes>
            ) {
                Log.d("AdminParkingListRepository", "[관리자] 주차 상세 정보: $response")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("AdminParkingListRepository", "[관리자] 주차 상세 정보: $responseBody")
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    Log.e("AdminParkingListRepository", "실패: ${response.message()}")
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<GetAdminParkingDetailRes>, t: Throwable) {
                Log.e("AdminParkingListRepository", "응답 실패: ${t.message}")
                onResult(Result.failure(t))
            }
        })
    }

    fun patchAdminParkingStatusOut (
        parkingId: Int,
        onResult: (Result<Int>) -> Unit
    ) {
        Log.d("AdminParkingListRepository", "[관리자] 주차 상세 정보 요청: $parkingId")

        adminParkingListService.patchAdminParkingOut(parkingId).enqueue(object : Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>
            ) {
                Log.d("AdminParkingListRepository", "[관리자] 주차 상태 변경: $response")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("AdminParkingListRepository", "[관리자] 주차 상태 변경: $responseBody")
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    Log.e("AdminParkingListRepository", "실패: ${response.message()}")
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("AdminParkingListRepository", "응답 실패: ${t.message}")
                onResult(Result.failure(t))
            }
        })
    }

}
