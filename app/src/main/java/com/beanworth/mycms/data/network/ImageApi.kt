package com.beanworth.mycms.data.network

import com.beanworth.mycms.data.responses.Image
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ImageApi {

    @Multipart
    @POST("/api/origination/v1/my-images/")
    suspend fun post(@Part image:MultipartBody.Part,@PartMap() partmap:MutableMap<String,RequestBody>):Image

    @GET("/api/origination/v1/my-images/")
    suspend fun get():List<Image>
}