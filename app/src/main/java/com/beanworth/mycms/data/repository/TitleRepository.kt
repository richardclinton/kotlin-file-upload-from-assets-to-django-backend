package com.beanworth.mycms.data.repository

import com.beanworth.mycms.data.network.TitleApi

class TitleRepository(
    private val api: TitleApi
):BaseRepository() {
    suspend fun title() = safeApiCall {
        api.title()
    }
}