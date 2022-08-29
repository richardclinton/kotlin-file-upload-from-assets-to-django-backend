package com.beanworth.mycms.data.network

import okhttp3.ResponseBody

sealed class Resource <out T>{
    data class Success<T>(var value:T):Resource<T>()
    data class Failure(
        val isNetworkError:Boolean,
        val errorCode:Int?,
        val errorBody: ResponseBody?
    ):Resource<Nothing>()
//    data class Clinton(
//    val error:String?
//    ):Resource<Nothing>()
    object loading:Resource<Nothing>()
}