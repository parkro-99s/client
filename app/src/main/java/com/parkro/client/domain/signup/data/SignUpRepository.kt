package com.parkro.client.domain.signup.data

import android.util.Log
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.signup.api.PostSignUpReq
import com.parkro.client.domain.signup.api.SignUpService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
private const val TAG = "SignUpService"

class SignUpRepository {
    // RetrofitClient 기반으로 인스턴스 생성 후 ExampleService 구현체 전달
    private val signUpService: SignUpService by lazy {
        RetrofitClient.instance.create(SignUpService::class.java)
    }

    // 중복 아이디 조회
    fun getUsername(username: String, onResult: (Result<ResponseBody>) -> Unit) {
        val call = signUpService.getUsername(username)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<ResponseBody> {

            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onResult(Result.success(response.body()!!))
                } else {
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    fun postSignUp(
        postSignUpReq: PostSignUpReq,
        onResult: (Result<String>) -> Unit
    ) {
        val call = signUpService.postSignUp(postSignUpReq)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("Response body is null")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onResult(Result.failure(Throwable("Error: ${response.message()}, Body: $errorBody")))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Log the failure
                Log.e(TAG, "Sign up request failed: ${t.message}", t)
                onResult(Result.failure(t))
            }
        })
    }

}