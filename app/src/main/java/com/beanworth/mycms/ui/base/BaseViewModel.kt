package com.beanworth.mycms.ui.base

import androidx.lifecycle.ViewModel
import com.beanworth.mycms.data.repository.BaseRepository

abstract class BaseViewModel(
    private val repository: BaseRepository
):ViewModel() {
}