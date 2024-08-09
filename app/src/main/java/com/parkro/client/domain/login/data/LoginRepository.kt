package com.parkro.client.domain.login.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.parkro.client.domain.login.api.PostLoginReq
import com.parkro.client.domain.login.api.LoginService
import com.parkro.client.domain.login.api.PostLoginRes
import com.parkro.client.domain.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * 로그인
 *
 * @author 양재혁
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   양재혁      최초 생성
 * </pre>
 */
class LoginRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ExampleService 구현체 전달
    private val loginService: LoginService by lazy {
        RetrofitClient.instance.create(LoginService::class.java)
    }

    /* 로그인 */
    fun postLogin(postLoginReq: PostLoginReq, onResult: (Result<PostLoginRes>) -> Unit) {
        // Fetch FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onResult(Result.failure(Throwable("Fetching FCM registration token failed")))
                return@addOnCompleteListener
            }
            // 새로운 FCM Token 발급
            val fcmToken = task.result

            // Proceed with login request
            val call = loginService.postLogin(fcmToken, postLoginReq)
            call.enqueue(object : Callback<PostLoginRes> {
                override fun onResponse(call: Call<PostLoginRes>, response: Response<PostLoginRes>) {
                    if (response.isSuccessful) {
                        val token = response.headers()["Authorization"]
                        val username = response.body()?.username
                        val carProfile = response.body()?.carProfile
                        if (token != null && username != null && carProfile != null) {
                            val postLoginRes = PostLoginRes(token, username, carProfile)
                            // Return the result with success
                            onResult(Result.success(postLoginRes))
                        } else {
                            onResult(Result.failure(Throwable("Authorization header or response body is missing")))
                        }
                    } else {
                        onResult(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: Call<PostLoginRes>, t: Throwable) {
                    onResult(Result.failure(t))
                }
            })
        }
    }
}

