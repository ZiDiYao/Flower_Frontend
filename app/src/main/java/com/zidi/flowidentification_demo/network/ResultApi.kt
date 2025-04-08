package com.zidi.flowidentification_demo.network


import com.zidi.flowidentification_demo.model.IdentificationResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ResultApi {
    @GET("/api/history/{userId}")
    fun getHistory(@Path("userId") userId: String): Call<List<IdentificationResult>>
}
