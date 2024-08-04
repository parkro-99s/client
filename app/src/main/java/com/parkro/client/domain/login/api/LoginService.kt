package com.parkro.client.domain.login.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {

    // API 경로 지정 및 응답 데이터 지정
    @POST("/member/sign-in")
    fun postLogin(@Header("FCM-TOKEN") token: String?, @Body postLoginReq: PostLoginReq): Call<PostLoginRes>


}