package com.beanworth.mycms.data.repository

import android.util.Log
import com.beanworth.mycms.data.network.ImageApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

class ImageRepository(private val api:ImageApi): BaseRepository() {
    suspend fun post(image:MultipartBody.Part, map: MutableMap<String,RequestBody>) = safeApiCall {
        Log.i("FileRepo", image.toString())
        api.post(image,map)
    }
    suspend fun get() = safeApiCall {
        api.get()
    }
}