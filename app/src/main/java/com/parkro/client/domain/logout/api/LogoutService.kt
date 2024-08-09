package com.parkro.client.domain.logout.api

import com.parkro.client.domain.login.api.PostLoginReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
/**
 * 로그아웃 도메인
 *
 * @author 양재혁
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   양재혁      최초 생성
 * 2024.08.02   양재혁      로그아웃 API
 * </pre>
 */
interface LogoutService {
    /**
     * 로그아웃
     */
    @POST("/member/sign-out")
    fun postLogout(): Call<Void>

}