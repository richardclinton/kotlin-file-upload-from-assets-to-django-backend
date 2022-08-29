package com.beanworth.mycms.ui

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.beanworth.mycms.R
import com.beanworth.mycms.data.network.ImageApi
import com.beanworth.mycms.data.network.RemoteDatSource
import com.beanworth.mycms.data.network.Resource
import com.beanworth.mycms.data.repository.ImageRepository
import com.beanworth.mycms.databinding.FragmentSecondBinding
import com.beanworth.mycms.ui.base.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import kotlin.reflect.typeOf

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    lateinit var viewModel:ImageViewModel

    private var _binding: FragmentSecondBinding? = null

    var selectedImageUri:Uri? = null
    var cameraUri:Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val remoteDatSource = RemoteDatSource()
        val repository = ImageRepository(remoteDatSource.buildApi(ImageApi::class.java))
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ImageViewModel::class.java)
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        var PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(requireContext(), *PERMISSIONS)){
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL)
        }
        binding.takePicture.setOnClickListener {
            fileUpload()
        }
        binding.uploadPicture.setOnClickListener {
            pickImageFromGallery()
        }
        binding.buttonUpload.setOnClickListener {
            val file = File(requireActivity().cacheDir, "myImage.png")
            file.createNewFile()
            file.outputStream().use {
                requireActivity().assets.open("Chips.png").copyTo(it)
            }
                viewModel.send(file)

        }
//        lifecycleScope.launch{
//            viewModel.get()
//        }
//        viewModel.image.observe(viewLifecycleOwner, Observer{ image ->
//            when(image){
//                is Resource.Success -> {
//                    Log.i("SuccessImage", image.value[0].image.javaClass.name)
//                }
//                is Resource.Failure -> {
//                    Log.i("FailureImage","%s,%s,%s".format(image.isNetworkError,
//                    image.errorCode,image.errorBody.toString()))
//                }
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val REQUEST_CODE = 200
        var PERMISSION_ALL = 1
        val IMAGE_PICK_CODE = 2
    }

    fun hasPermissions(context: Context,vararg permisions:String):Boolean = permisions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun fileUpload(){
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
        values.put(MediaStore.Images.Media.DESCRIPTION,
        "Photo taken on" + System.currentTimeMillis())
        cameraUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

//        used to open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(cameraIntent, REQUEST_CODE)


    }

    fun pickImageFromGallery(){
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageIntent.type = "image/*"
        val mimeTypes = arrayOf(
            "image/jpeg",
            "image/png"
        )
        pickImageIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(pickImageIntent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            Log.i("CameraCapture",cameraUri.toString())
            selectedImageUri = cameraUri
            Log.i("ImagePick",selectedImageUri.toString())
            lifecycleScope.launch {
                viewModel.post(requireContext(), selectedImageUri!!)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            selectedImageUri = data?.data
            Log.i("ImagePick2", selectedImageUri.toString())
            lifecycleScope.launch {
                viewModel.post(requireContext(), selectedImageUri!!)


            }
        }
    }
}