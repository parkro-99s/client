package com.parkro.client.domain.payment.data

import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.payment.api.GetCurrentParkingInfo
import com.parkro.client.domain.payment.api.GetMemberCouponList
import com.parkro.client.domain.payment.api.PaymentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ExampleService 구현체 전달
    private val paymentService: PaymentService by lazy {
        RetrofitClient.instance.create(PaymentService::class.java)
    }

    fun findParkingInfoFirst(username: String, onResult: (Result<GetCurrentParkingInfo>) -> Unit) {
        val call = paymentService.findParkingInfoFirst(username)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<GetCurrentParkingInfo> {
            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<GetCurrentParkingInfo>, response: Response<GetCurrentParkingInfo>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    println("실패: " + response.message())
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<GetCurrentParkingInfo>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    fun getMemberCouponList(username: String, onResult: (Result<GetMemberCouponList>) -> Unit) {
        val call = paymentService.getMemberCouponList(username)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<GetMemberCouponList> {
            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<GetMemberCouponList>, response: Response<GetMemberCouponList>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    println("실패: " + response.message())
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<GetMemberCouponList>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
