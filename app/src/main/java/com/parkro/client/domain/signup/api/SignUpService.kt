package com.parkro.client.domain.signup.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
/**
 * 회원 가입 도메인
 *
 * @author 양재혁
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   양재혁      최초 생성
 * </pre>
 */
interface SignUpService {

    /**
     * username 중복 체크
     * @param username
     * @return ResponseBody
     */
    @GET("/member")
    fun getUsername(@Query("user") username: String): Call<ResponseBody>

    /**
     * 회원가입
     * @param PostSignUpReq
     * @return ResponseBody
     */
    @POST("/member/sign-up")
    fun postSignUp(@Body postSignUpReq: PostSignUpReq): Call<ResponseBody>
}