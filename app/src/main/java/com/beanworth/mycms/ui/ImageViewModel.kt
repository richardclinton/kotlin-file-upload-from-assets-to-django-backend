package com.beanworth.mycms.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beanworth.mycms.data.network.Resource
import com.beanworth.mycms.data.repository.ImageRepository
import com.beanworth.mycms.data.responses.Image
import com.beanworth.mycms.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.http.Multipart
import java.io.File

class ImageViewModel(
    private val repository: ImageRepository
):BaseViewModel(repository) {

    val _image:MutableLiveData<Resource<List<Image>>> = MutableLiveData()
    val image:LiveData<Resource<List<Image>>>
    get() = _image

    suspend fun get() = viewModelScope.launch {
        _image.value = repository.get()
    }

    suspend fun post(context: Context,imageUri: Uri){
        val uriPathHelper = UriPathHelper()
        Log.i("ImageUrii", imageUri.toString())
        val filePath = uriPathHelper.getPath(context, imageUri!!)
        Log.i("FilePath", filePath.toString())
        val file = File(filePath)
        val requestFile:RequestBody = file.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multiparty = MultipartBody.Part.createFormData("file", file.name,requestFile)

        val data = JSONObject()
        data.put("name", "clinton")
        val body = data.toString().toRequestBody("application/json".toMediaTypeOrNull())

//        repository.post(multiparty,body)
    }

    private fun createPartFromString(stringData:String):RequestBody{
            return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }

   fun send(file:File){
       Log.i("File",file.absolutePath)
       val map:MutableMap<String,RequestBody> = mutableMapOf()
//       val requestfile:RequestBody = file.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//       val multipart = MultipartBody.Part.createFormData("image", file.name,requestfile)
        val file2 = MultipartBody.Part
            .createFormData(name = "image",
            filename = file.name,
            file.asRequestBody())
//       val data = JSONObject()
//       data.put("name", "clinton")
//       val body = data.toString().toRequestBody("application/json".toMediaTypeOrNull())
       val name = createPartFromString("clinton")
       map.put("name", name)
       viewModelScope.launch {
           repository.post(file2,map)
       }
   }
}