package com.wahyuhw.userstoryapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.databinding.ActivityMainBinding
import com.wahyuhw.userstoryapp.ui.adapter.ListStoryAdapter
import com.wahyuhw.userstoryapp.ui.adapter.LoadingStateAdapter
import com.wahyuhw.userstoryapp.utils.showShortToast
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding
    private var doubleBackToExit = false
    private val adapter = ListStoryAdapter()

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            loadData()
        }

        with(binding) {
            btnMaps.setOnClickListener {
                startActivity(Intent(applicationContext, MapsActivity::class.java))
            }
            btnBookmark.setOnClickListener {
                startActivity(Intent(applicationContext, BookmarkActivity::class.java))
            }
            btnProfile.setOnClickListener {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
            }
            btnAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                uploadStory.launch(intent)
            }
        }
    }

    private suspend fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvStory.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })

        try {
            viewModel.getPagedStory().observe(this@MainActivity) { data ->
                adapter.submitData(lifecycle, data)
            }
            binding.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            e.message?.let { showShortToast(this@MainActivity, it) }
            binding.progressBar.visibility = View.GONE
        }

    }

    private val uploadStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == REQUEST_CODE) {
            val message = it.data?.getStringExtra(EXTRA_UPLOAD)
            if (message == UPLOAD_SUCCESS) {
                adapter.refresh()
            }
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
        const val REQUEST_CODE = 1
        const val EXTRA_UPLOAD = "extra_upload"
        const val UPLOAD_SUCCESS = "Uploaded!"
    }
}