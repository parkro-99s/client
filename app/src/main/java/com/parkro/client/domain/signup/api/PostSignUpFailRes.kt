package com.parkro.client.domain.signup.api

// 회원 가입 실패
data class PostSignUpFailRes (

    val status: Int,
    val errorCode: String,
    val message: String

)
