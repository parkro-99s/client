package com.parkro.client.domain.mypage.api

import com.parkro.client.domain.login.api.PostLoginReq
import com.parkro.client.domain.login.api.PostLoginRes
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MypageService {

    @PATCH("/member/{username}")
    fun patchUserDetails(@Path("username") username: String): Call<ResponseBody>

    @PATCH("/member/{username}/car")
    fun patchCarDetails(@Path("username") username: String): Call<ResponseBody>

    @GET("/member/{username}")
    fun getUserDetails(@Path("username") username: String): Call<GetUserDetailsRes>

    @PATCH("/member/car")
    fun postCarDetails(@Body postCarReq: PostCarReq): Call<ResponseBody>

}