package com.parkro.client.domain.payment.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReceiptService {

    /**
     * 바코드로 인식된 영수증 정보 조회
     * @param receiptId
     * @return 성공 여부
     */
    @GET("/receipt/{receiptId}")
    fun getMemberReceiptInfo(@Path("receiptId") receiptId: Int): Call<GetReceiptRes>
}