package com.zidi.flowidentification_demo.network

import com.zidi.flowidentification_demo.model.LoginRequest
import com.zidi.flowidentification_demo.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest?): Call<LoginResponse?>?

    @POST("/api/auth/register")
    fun register(@Body request: LoginRequest?): Call<LoginResponse?>?

}
