package com.parkro.client.domain.mypage.api

import com.parkro.client.domain.login.api.PostLoginReq
import com.parkro.client.domain.login.api.PostLoginRes
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MypageService {

    @PATCH("/member/{username}")
    fun patchUserDetails(@Path("username") username: String): Call<ResponseBody>
}