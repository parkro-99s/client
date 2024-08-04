package com.parkro.client.domain.signup.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SignUpService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/member")
    fun getUsername(@Query("user") username: String): Call<ResponseBody>

    @POST("/member/sign-up")
    fun postSignUp(@Body postSignUpReq: PostSignUpReq): Call<ResponseBody>
}