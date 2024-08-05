package com.parkro.client.domain.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.parkro.client.BuildConfig
import com.parkro.client.util.PreferencesUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

// 서버 url을 설정하고 데이터 파싱 및 객체 정보를 반환할 수 있는 인스턴스 제공
// object로 선언해 싱글톤으로 작동
object RetrofitClient {

    private const val BASE_URL = BuildConfig.SERVER_BASEURL // 서버 URL
    private const val AUTH_HEADER = "Authorization"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val token = PreferencesUtil.getAccessToken(null)
//                Log.d("token","token+$token")
//
//                val request: Request = chain.request().newBuilder()
//                    .addHeader(AUTH_HEADER, "$token")
//                    .build()
//
//                chain.proceed(request)
//            }
            .build()
    }

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}
