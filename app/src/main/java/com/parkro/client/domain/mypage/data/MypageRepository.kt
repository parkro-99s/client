package com.parkro.client.domain.mypage.data

import android.util.Log
import com.parkro.client.domain.mypage.api.MypageService
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.domain.signup.api.PostSignUpReq
import com.parkro.client.util.PreferencesUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageRepository {

    private val mypageService: MypageService by lazy {
        RetrofitClient.instance.create(MypageService::class.java)
    }

    private val username = PreferencesUtil.getUsername(null)

    fun patchUserDetails(
        username: PostSignUpReq,
        onResult: (Result<Int>) -> Unit
    ) {
        val call = mypageService.patchUserDetails(username = )
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