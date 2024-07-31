package com.parkro.client.domain.example.data

import com.google.firebase.messaging.FirebaseMessaging
import com.parkro.client.domain.example.api.PostLoginReq
import com.parkro.client.domain.example.api.LoginService
import com.parkro.client.domain.example.api.PostLoginRes
import com.parkro.client.domain.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ExampleService 구현체 전달
    private val loginService: LoginService by lazy {
        RetrofitClient.instance.create(LoginService::class.java)
    }

    fun postLoginInfo(postLoginReq: PostLoginReq, onResult: (Result<PostLoginRes>) -> Unit) {
        // Fetch FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onResult(Result.failure(Throwable("Fetching FCM registration token failed")))
                return@addOnCompleteListener
            }
            // 새로운 FCM Token 발급
            val fcmToken = task.result

            // Proceed with login request
            val call = loginService.postLoginInfo(fcmToken, postLoginReq)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val token = response.headers()["Authorization"]
                        val username = response.body()
                        if (token != null && username != null) {
                            onResult(Result.success(PostLoginRes(token, username)))
                        } else {
                            onResult(Result.failure(Throwable("Authorization header or response body is missing")))
                        }
                    } else {
                        onResult(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    onResult(Result.failure(t))
                }
            })
        }
    }
}

