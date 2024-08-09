package com.parkro.client.domain.logout.data

import android.util.Log
import com.parkro.client.domain.logout.api.LogoutService
import com.parkro.client.domain.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * 로그아웃
 *
 * @author 양재혁
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   양재혁      최초 생성
 * </pre>
 */
class LogoutRepository {
    private val logoutService: LogoutService by lazy {
        RetrofitClient.instance.create(LogoutService::class.java)
    }

    /* 로그아웃 */
    fun postLogout(
        onResult: (Result<Void>) -> Unit
    ) {
        val call = logoutService.postLogout()
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    in 200..299 -> {
                        // 성공적인 응답 (2xx)
                        Log.d("responsebody", "response+${response.body()}")
                        val responseBody = response.body()
                        onResult(Result.success(responseBody) as Result<Void>)
                    }
                    400 -> {
                        // 클라이언트 오류 (4xx)
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Client Error: ${response.message()}, Body: $errorBody")))
                    }
                    401 -> {
                        // 인증 실패 (401 Unauthorized)
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Unauthorized: ${response.message()}, Body: $errorBody")))
                    }
                    403 -> {
                        // 권한 없음 (403 Forbidden)
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Forbidden: ${response.message()}, Body: $errorBody")))
                    }
                    404 -> {
                        // 리소스 없음 (404 Not Found)
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Not Found: ${response.message()}, Body: $errorBody")))
                    }
                    500 -> {
                        // 서버 오류 (5xx)
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Server Error: ${response.message()}, Body: $errorBody")))
                    }
                    else -> {
                        // 기타 상태 코드
                        val errorBody = response.errorBody()?.string()
                        onResult(Result.failure(Throwable("Unexpected Error: ${response.message()}, Body: $errorBody")))
                    }
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Log the failure
                onResult(Result.failure(t))
            }
        })
    }
}