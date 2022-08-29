package com.beanworth.mycms.ui.base
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beanworth.mycms.data.repository.BaseRepository
import com.beanworth.mycms.data.repository.ImageRepository
import com.beanworth.mycms.data.repository.TitleRepository
import com.beanworth.mycms.ui.ImageViewModel
import com.beanworth.mycms.ui.TitleViewModel

class ViewModelFactory(
    private val repository: BaseRepository
):ViewModelProvider.NewInstanceFactory() {
    override fun <T:ViewModel?> create(modelclass:Class<T>):T{
        return when{
            modelclass.isAssignableFrom(TitleViewModel::class.java) -> TitleViewModel(repository as TitleRepository) as T
            modelclass.isAssignableFrom(ImageViewModel::class.java) -> ImageViewModel(repository as ImageRepository) as T
            else -> throw IllegalAccessException("ViewModel class Not Found")
        }
    }
}