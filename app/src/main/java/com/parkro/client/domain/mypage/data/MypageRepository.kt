package com.parkro.client.domain.mypage.data

import android.util.Log
import com.parkro.client.domain.mypage.api.GetUserDetailsRes
import com.parkro.client.domain.mypage.api.MypageService
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.util.PreferencesUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageRepository {

    private val mypageService: MypageService by lazy {
        RetrofitClient.instance.create(MypageService::class.java)
    }

    private val username: String? = PreferencesUtil.getUsername(null)

    fun patchUserDetails(
        onResult: (Result<Int>) -> Unit
    ) {
        val username = username ?: return onResult(Result.failure(Throwable("Username is null")))

        val call = mypageService.patchUserDetails(username)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        try {
                            val responseString = responseBody.string()
                            val result = responseString.toInt()
                            onResult(Result.success(result))
                        } catch (e: Exception) {
                            onResult(Result.failure(Throwable("Failed to parse response body", e)))
                        }
                    } else {
                        onResult(Result.failure(Throwable("Response body is null")))
                    }
                } else {
                        onResult(Result.failure(Throwable("Response body is null")))
                    }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    fun patchCarDetails(
        onResult: (Result<String>) -> Unit
    ) {
        val username = username ?: return onResult(Result.failure(Throwable("Username is null")))

        val call = mypageService.patchCarDetails(username)
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
                onResult(Result.failure(t))
            }
        })
    }

    fun getUserDetails(
        onResult: (Result<GetUserDetailsRes>) -> Unit
    ) {
        val username = username ?: return onResult(Result.failure(Throwable("Username is null")))

        val call = mypageService.getUserDetails(username)
        call.enqueue(object : Callback<GetUserDetailsRes> {
            override fun onResponse(call: Call<GetUserDetailsRes>, response: Response<GetUserDetailsRes>) {
                if (response.isSuccessful) {
                    val getUserDetailsRes = response.body()
                    if (getUserDetailsRes != null) {
                        onResult(Result.success(getUserDetailsRes))
                    } else {
                        onResult(Result.failure(Throwable("Response body is null")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onResult(Result.failure(Throwable("Error: ${response.message()}, Body: $errorBody")))
                }
            }

            override fun onFailure(call: Call<GetUserDetailsRes>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
