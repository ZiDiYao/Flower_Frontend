package com.zidi.flowidentification_demo.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.zidi.flowidentification_demo.model.FlowerDescription

interface DescriptionApi {
    @POST("/api/description/save")
    fun saveDescription(@Body data: FlowerDescription): Call<ResponseBody>
}
