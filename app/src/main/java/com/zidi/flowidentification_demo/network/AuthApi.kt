package com.zidi.flowidentification_demo.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.zidi.flowidentification_demo.model.LoginRequest
import com.zidi.flowidentification_demo.model.LoginResponse

interface AuthApi {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
