package com.parkro.client.domain.logout.api

import com.parkro.client.domain.login.api.PostLoginReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LogoutService {
    @POST("/member/sign-out")
    fun postLogout(): Call<Void>

}