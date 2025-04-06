package com.zidi.flowidentification_demo.network

import com.zidi.flowidentification_demo.model.FlowerDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DescriptionApi {
    @POST("/saveDescription")
    fun saveDescription(@Body data: FlowerDescription): Call<ResponseBody>
}
