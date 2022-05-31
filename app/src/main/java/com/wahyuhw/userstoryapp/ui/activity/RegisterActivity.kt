package com.wahyuhw.userstoryapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.response.ApiResponse
import com.wahyuhw.userstoryapp.databinding.ActivityRegisterBinding
import com.wahyuhw.userstoryapp.utils.showLongToast
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory

class RegisterActivity : AppCompatActivity(), ResponseCallback<ApiResponse> {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding as ActivityRegisterBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            setupRegister()
        }

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRegister() {
        with(binding) {
            if (edtUsername.error == null && edtEmail.error == null && edtPassword.error == null) {
                val username = edtUsername.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                viewModel.register(username, email, password).observe(this@RegisterActivity) {
                    when (it) {
                        is ResponseResource.Loading -> onLoading()
                        is ResponseResource.Success -> it.data?.let { it1 -> onSuccess(it1) }
                        is ResponseResource.Error -> onFailed(it.message)
                    }
                }
            }
        }
    }

    override fun onSuccess(data: ApiResponse) {
        binding.progressBar.visibility = gone
        data.message?.let { showLongToast(this, it) }
        onBackPressed()
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message != null) {
            showLongToast(this@RegisterActivity, message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}