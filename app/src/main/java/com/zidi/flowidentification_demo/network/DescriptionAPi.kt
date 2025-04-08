package com.zidi.flowidentification_demo.network

import com.zidi.flowidentification_demo.model.IdentificationResult
import com.zidi.flowidentification_demo.model.FlowerDescriptionRequest as FlowerDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DescriptionApi {

    @POST("/api/description/save")
    fun saveDescription(@Body data: FlowerDescription): Call<ResponseBody>

    // for getting history from backend

    @GET("/api/history/{userId}")
    fun getHistory(@Path("userId") userId: String): Call<List<IdentificationResult>>
}
