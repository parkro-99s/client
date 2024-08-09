package com.parkro.client.domain.payment.data

import android.util.Log
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.payment.api.GetCurrentParkingInfo
import com.parkro.client.domain.payment.api.GetMemberCouponList
import com.parkro.client.domain.payment.api.PaymentService
import com.parkro.client.domain.payment.api.PostPaymentReq
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 정산 관련 Repository
 *
 * @author 김지수
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   김지수      최초 생성
 * 2024.07.31   김지수      현재 주차중인, 정산 필요한 주차 정보 상세 조회
 * 2024.08.02   김지수      사용자 쿠폰 리스트 조회
 * 2024.08.04   김지수      실제 결제 등록
 * </pre>
 */
class PaymentRepository {

    private val paymentService: PaymentService by lazy {
        RetrofitClient.instance.create(PaymentService::class.java)
    }

    // 현재 주차중인, 정산 필요한 주차 정보 상세 조회
    fun findParkingInfoFirst(username: String, onResult: (Result<GetCurrentParkingInfo>) -> Unit) {
        val call = paymentService.getParkingInfoFirst(username)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<GetCurrentParkingInfo> {
            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<GetCurrentParkingInfo>, response: Response<GetCurrentParkingInfo>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("PaymentRepository", "주차 정보: $responseBody")
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

    // 사용자 쿠폰 리스트 조회
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

    // 실제 결제 등록
    fun addPayment(paymentRequest: PostPaymentReq, onResult: (Result<Int>) -> Unit) {
        val call = paymentService.addPayment(paymentRequest)

        call.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("응답 바디가 null입니다.")))
                    }
                } else {
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
