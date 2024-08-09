package com.parkro.client.domain.login.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
/**
 * 로그인 도메인
 *
 * @author 양재혁
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   양재혁      최초 생성
 * 2024.07.31   양재혁      로그인 API
 * </pre>
 */
interface LoginService {
    /**
     * 로그인
     * @param PostLoginReq
     * @return 로그인 정보 {@link PostLoginRes}
     */
    @POST("/member/sign-in")
    fun postLogin(@Header("FCM-TOKEN") token: String?, @Body postLoginReq: PostLoginReq): Call<PostLoginRes>


}