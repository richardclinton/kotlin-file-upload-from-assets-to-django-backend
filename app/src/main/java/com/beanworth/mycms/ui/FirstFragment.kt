package com.beanworth.mycms.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.beanworth.mycms.R
import com.beanworth.mycms.data.network.RemoteDatSource
import com.beanworth.mycms.data.network.Resource
import com.beanworth.mycms.data.network.TitleApi
import com.beanworth.mycms.data.repository.TitleRepository
import com.beanworth.mycms.data.responses.Title
import com.beanworth.mycms.databinding.FragmentFirstBinding
import com.beanworth.mycms.ui.base.ViewModelFactory
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    lateinit var viewModel:TitleViewModel
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val remoteDatSource = RemoteDatSource()
        val repository = TitleRepository(remoteDatSource.buildApi(TitleApi::class.java))
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(TitleViewModel::class.java)
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        lifecycleScope.launch {
            viewModel.getTitle()
        }
        viewModel.title.observe(viewLifecycleOwner, Observer{ title ->
            when(title){
                is Resource.Success -> {
                    Log.i("Success","%s".format(title.value.toString()))
                }
                is Resource.Failure -> {
                    Log.i("Failure","%s,%s,%s".format(title.errorCode,title.errorBody,title.isNetworkError))
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}