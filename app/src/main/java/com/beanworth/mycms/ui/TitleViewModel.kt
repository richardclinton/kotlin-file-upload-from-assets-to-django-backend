package com.beanworth.mycms.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beanworth.mycms.data.network.Resource
import com.beanworth.mycms.data.repository.TitleRepository
import com.beanworth.mycms.data.responses.Title
import com.beanworth.mycms.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class TitleViewModel(
    private val repository: TitleRepository
    ):BaseViewModel(repository) {

    val _title:MutableLiveData<Resource<List<Title>>> = MutableLiveData()
    val title:LiveData<Resource<List<Title>>>
    get() = _title

        suspend fun getTitle() = viewModelScope.launch {
                    _title.value = repository.title()
        }
}