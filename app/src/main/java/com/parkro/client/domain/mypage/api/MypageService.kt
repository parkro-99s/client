package com.parkro.client.domain.mypage.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
/**
 * 마이 페이지 도메인
 *
 * @author 양재혁
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   양재혁      최초 생성
 * 2024.08.02   양재혁      회원 탈퇴 API
 * 2024.08.02   양재혁      차량 삭제 API
 * 2024.08.02   양재혁      회원 정보 조회 API
 * 2024.08.04   양재혁      차량 등록 API
 * 2024.08.04   양재혁      회원 정보 수정 API
 * </pre>
 */
interface MypageService {

    /**
     * 회원 탈퇴
     * @param username
     * @return ResponseBody
     */
    @PATCH("/member/{username}")
    fun patchUserDetails(@Path("username") username: String): Call<ResponseBody>

    /**
     * 차량 삭제
     * @param username
     * @return ResponseBody
     */
    @PATCH("/member/{username}/car")
    fun patchCarDetails(@Path("username") username: String): Call<ResponseBody>

    /**
     * 회원 정보 조회
     * @param username
     * @return 회원 정보 {@link GetUserDetailsRes}
     */
    @GET("/member/{username}")
    fun getUserDetails(@Path("username") username: String): Call<GetUserDetailsRes>

    /**
     * 차량 등록
     * @param PostCarReq
     * @return ResponseBody
     */
    @PATCH("/member/car")
    fun postCarDetails(@Body postCarReq: PostCarReq): Call<ResponseBody>

    /**
     * 회원 정보 수정
     * @param username
     * @param PutModifiedUserDetailsReq
     * @return ResponseBody
     */
    @PUT("/member/{username}")
    fun putUserDetails(
        @Path("username") username: String,
        @Body request: PutModifiedUserDetailsReq
    ): Call<ResponseBody>
}