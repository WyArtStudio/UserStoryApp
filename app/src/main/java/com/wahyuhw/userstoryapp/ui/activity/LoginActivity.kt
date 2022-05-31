package com.wahyuhw.userstoryapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.response.LoginResponse
import com.wahyuhw.userstoryapp.data.room.UserEntity
import com.wahyuhw.userstoryapp.databinding.ActivityLoginBinding
import com.wahyuhw.userstoryapp.utils.showLongToast
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity(), ResponseCallback<LoginResponse> {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding as ActivityLoginBinding
    private var doubleBackToExit = false

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            setupLogin()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity)
                    .toBundle())
        }
    }

    private fun setupLogin() {
        with(binding) {
            if (edtEmail.error == null && edtPassword.error == null) {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                viewModel.login(email, password).observe(this@LoginActivity) { user ->
                    when (user) {
                        is ResponseResource.Loading -> onLoading()
                        is ResponseResource.Success -> user.data?.let { onSuccess(it) }
                        is ResponseResource.Error -> onFailed(user.message)
                    }
                }
            }
        }
    }

    override fun onSuccess(data: LoginResponse) {
        binding.progressBar.visibility = gone

        val loginResult = data.loginResult

        val token = loginResult?.token
        val user = loginResult?.userId?.let { id -> loginResult.name?.let { name ->
            UserEntity(id, name) } }

        // TODO: Delete after Successful Testing
        if (token != null) {
            viewModel.saveToken(token)
        } else {
            showLongToast(applicationContext, "Token null")
        }

        viewModel.saveSession(true)

        if (user != null) {
            viewModel.insertUser(user)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
        finishAffinity()
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message != null) {
            showLongToast(this@LoginActivity, message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finish()
            exitProcess(0)
        }

        this.doubleBackToExit = true
        Toast.makeText(this, resources.getText(R.string.click_exit), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExit = false }, delay)
    }

    companion object {
        const val delay = 2000L
    }
}