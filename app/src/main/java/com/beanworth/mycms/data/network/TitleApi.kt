package com.beanworth.mycms.data.network

import com.beanworth.mycms.data.responses.Title
import retrofit2.http.GET

interface TitleApi{
    @GET("/api/origination/v1/titles/")
    suspend fun title():List<Title>
}