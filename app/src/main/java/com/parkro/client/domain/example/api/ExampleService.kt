package com.parkro.client.domain.example.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ExampleService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/receipt/{receiptId}")
    fun getMemberReceiptInfo(@Path("receiptId") receiptId: Int): Call<ExampleDTO>
}