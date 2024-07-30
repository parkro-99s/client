package com.parkro.client.domain.network

import com.parkro.client.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 서버 url을 설정하고 데이터 파싱 및 객체 정보를 반환할 수 있는 인스턴스 제공
// object로 선언해 싱글톤으로 작동
object RetrofitClient {
    private const val BASE_URL = BuildConfig.SERVER_BASEURL // 서버 URL

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}