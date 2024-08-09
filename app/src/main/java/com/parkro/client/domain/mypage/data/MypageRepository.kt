package com.parkro.client.domain.mypage.data

import com.parkro.client.domain.mypage.api.GetUserDetailsRes
import com.parkro.client.domain.mypage.api.MypageService
import com.parkro.client.domain.mypage.api.PostCarReq
import com.parkro.client.domain.mypage.api.PutModifiedUserDetailsReq
import com.parkro.client.domain.network.RetrofitClient
import com.parkro.client.util.PreferencesUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * 마이페이지
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.03   양재혁      최초 생성
 * </pre>
 */
class MypageRepository {

    private val mypageService: MypageService by lazy {
        RetrofitClient.instance.create(MypageService::class.java)
    }

    private val username: String? = PreferencesUtil.getUsername(null)

    /* 회원 탈퇴 */
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

    /* 차량 삭제 */
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

    /* 회원 정보 조회 */
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

    /* 차량 등록 */
    fun postCarDetails(memberId: Int, carNumber: String, onResult: (Result<ResponseBody>) -> Unit) {
        val call = mypageService.postCarDetails(PostCarReq(memberId, carNumber))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("Received an empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error: ${response.message()}"
                    onResult(Result.failure(Throwable("$errorMessage, Body: $errorBody")))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    /* 회원 정보 수정 */
    fun putModifiedUserDetails(username: String, password: String, nickname: String, phoneNumber: String, carProfile: Int, onResult: (Result<ResponseBody>) -> Unit) {
        val call = mypageService.putUserDetails(username, PutModifiedUserDetailsReq(username, password, nickname, phoneNumber, carProfile))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("Received an empty response body")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error: ${response.message()}"
                    onResult(Result.failure(Throwable("$errorMessage, Body: $errorBody")))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

}
