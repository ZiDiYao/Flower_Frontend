package com.zidi.flowidentification_demo.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageMLAPI {
    @Multipart
    @POST("/api/expert1")
    fun predict(
        @Part image: MultipartBody.Part,
//        @Part("color") color : RequestBody,
//        @Part("petals") petals : RequestBody,
//        @Part("smell") smell : RequestBody,
//        @Part("location") location : RequestBody
    ): Call<ResponseBody>

    @POST("/api/expert2")
    fun predict2(
        @Body description: RequestBody
    ):Call<ResponseBody>

    @POST("/api/conflict")
    fun resolveConflict(
        @Body body: RequestBody
    ): Call<ResponseBody>
}