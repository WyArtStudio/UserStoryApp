package com.wahyuhw.userstoryapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.data.room.BookmarkStoryEntity
import com.wahyuhw.userstoryapp.databinding.ActivityBookmarkBinding
import com.wahyuhw.userstoryapp.ui.adapter.ListBookmarkAdapter
import com.wahyuhw.userstoryapp.utils.showLongToast
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkActivity : AppCompatActivity(), ResponseCallback<List<BookmarkStoryEntity>> {
    private var _binding: ActivityBookmarkBinding? = null
    private val binding get() = _binding as ActivityBookmarkBinding

    private lateinit var listStoryAdapter: ListBookmarkAdapter

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listStoryAdapter = ListBookmarkAdapter()
        binding.rvStory.apply {
            adapter = listStoryAdapter
            layoutManager = LinearLayoutManager(this@BookmarkActivity, LinearLayoutManager.VERTICAL, false)
        }

        loadData()

        binding.btnClear.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_clear).setCancelable(true).setPositiveButton(
                resources.getText(R.string.yes)) { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    binding.progressBar.visibility = visible
                    viewModel.clearBookmarkStory()
                    binding.progressBar.visibility = gone
                    loadData()
                }
            }.setNegativeButton(resources.getText(R.string.no)) { dialog, _ -> dialog.cancel() }
            builder.create().show()
        }

        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getListBookmarkStory().observe(this@BookmarkActivity) {
                when (it) {
                    is ResponseResource.Loading -> onLoading()
                    is ResponseResource.Success -> it.data?.let { it1 -> onSuccess(it1) }
                    is ResponseResource.Error -> onFailed(it.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRestart() {
        super.onRestart()
        listStoryAdapter.currentList.clear()
        loadData()
    }

    override fun onSuccess(data: List<BookmarkStoryEntity>) {
        binding.progressBar.visibility = gone
        listStoryAdapter.submitList(data)
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message != null) {
            showLongToast(this@BookmarkActivity, message)
        }
    }
}