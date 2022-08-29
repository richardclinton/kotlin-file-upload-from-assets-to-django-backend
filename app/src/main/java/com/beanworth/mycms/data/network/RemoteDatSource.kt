package com.beanworth.mycms.data.network

import com.beanworth.mycms.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDatSource{
    companion object{
//        private const val BASE_URL = "https://cms-dev2.vuup.co.tz"
        private const val BASE_URL = "https://da1e-196-41-61-34.ngrok.io"
    }

    fun <Api> buildApi(
        api:Class<Api>
    ):Api{

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
            OkHttpClient.Builder()
                .also { client ->
                    if (BuildConfig.DEBUG){
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}