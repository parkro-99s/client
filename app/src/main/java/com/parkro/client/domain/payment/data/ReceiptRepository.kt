package com.parkro.client.domain.payment.data

import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.payment.api.GetReceiptRes
import com.parkro.client.domain.payment.api.ReceiptService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceiptRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ReceiptService 구현체 전달
    private val receiptService: ReceiptService by lazy {
        RetrofitClient.instance.create(ReceiptService::class.java)
    }

    fun getMemberReceiptInfo(receiptId: Int, onResult: (Result<GetReceiptRes>) -> Unit) {
        val call = receiptService.getMemberReceiptInfo(receiptId)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<GetReceiptRes> {

            // 응답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<GetReceiptRes>, response: Response<GetReceiptRes>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(Result.success(it))
                    } ?: run {
                        onResult(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    // 서버 응답 코드가 200이 아닌 경우, 에러 메시지를 포함하여 전달
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onResult(Result.failure(Throwable(errorMessage)))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<GetReceiptRes>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
