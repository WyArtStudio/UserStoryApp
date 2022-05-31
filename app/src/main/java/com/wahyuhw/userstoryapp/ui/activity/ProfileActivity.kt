package com.wahyuhw.userstoryapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.room.UserEntity
import com.wahyuhw.userstoryapp.databinding.ActivityProfileBinding
import com.wahyuhw.userstoryapp.utils.showLongToast
import com.wahyuhw.userstoryapp.utils.vectorToBitmap
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity(), ResponseCallback<UserEntity> {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding as ActivityProfileBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            val token = viewModel.getToken().first()
            binding.tvToken.text = token
            viewModel.getUser().observe(this@ProfileActivity) { response ->
                when (response) {
                    is ResponseResource.Error -> onFailed(response.message)
                    is ResponseResource.Loading -> onLoading()
                    is ResponseResource.Success -> response.data?.let {
                            responseData -> onSuccess(responseData)
                    }
                }
            }
        }

        with(binding) {
            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnLogout.setOnClickListener {
                val builder = AlertDialog.Builder(this@ProfileActivity)
                builder.setMessage(R.string.dialog_logout).setCancelable(true).setPositiveButton(
                    resources.getText(R.string.yes)) { _, _ ->
                    setupLogout()
                }.setNegativeButton(resources.getText(R.string.no)) { dialog, _ -> dialog.cancel() }
                builder.create().show()
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun setupLogout() {
        viewModel.clearUser()
        viewModel.saveToken("")
        viewModel.saveSession(false)
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSuccess(data: UserEntity) {
        binding.progressBar.visibility = gone
        bind(data)
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message != null) {
            showLongToast(this@ProfileActivity, message)
        }
    }

    private fun bind(user: UserEntity) {
        with(binding) {
            tvUsername.text = user.name
            tvUserid.text = user.userId
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}