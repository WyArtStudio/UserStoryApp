package com.wahyuhw.userstoryapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.data.room.BookmarkStoryEntity
import com.wahyuhw.userstoryapp.databinding.ActivityDetailBinding
import com.wahyuhw.userstoryapp.utils.showLongToast
import com.wahyuhw.userstoryapp.utils.showShortToast
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), ResponseCallback<BookmarkStoryEntity> {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding as ActivityDetailBinding
    private lateinit var story: BookmarkStoryEntity

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<StoryItem>(EXTRA_STORY)!!

        with(detailStory) {
            story = BookmarkStoryEntity(id, photoUrl, createdAt, name, description, lon, lat, isBookmark)
        }

        CoroutineScope(Dispatchers.Main).launch {
            story.name?.let {
                viewModel.getDetailStory(it).observe(this@DetailActivity) { response ->
                    when (response) {
                        is ResponseResource.Error -> onFailed(response.message)
                        is ResponseResource.Loading -> onLoading()
                        is ResponseResource.Success -> response.data?.let {
                                responseData -> onSuccess(responseData) }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSuccess(data: BookmarkStoryEntity) {
        binding.progressBar.visibility = gone
        bind(data)
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message == EXTRA_STORY) {
            bind(story)
        } else {
            if (message != null) {
                showLongToast(this@DetailActivity, message)
            }
        }
    }

    private fun bind(data: BookmarkStoryEntity) {
        with(binding) {
            Glide.with(this@DetailActivity).load(data.photoUrl).into(imgPicture)
            tvDescription.text = data.description
            tvName.text = data.name

            val date = data.createdAt?.substring(0, 10)
            tvDate.text = date

            btnFavorite.isFavorite = data.isBookmark == true
            btnFavorite.setOnFavoriteChangeListener { _, isFavorite ->
                if (isFavorite) {
                    btnFavorite.setAnimateFavorite(true)
                    data.isBookmark = true
                    data.let { it1 -> viewModel.insertBookmarkStory(it1) }
                    showShortToast(this@DetailActivity,
                        resources.getText(R.string.added_favorite) as String)
                } else {
                    data.isBookmark = false
                    viewModel.deleteBookmarkStory(story)
                    showShortToast(this@DetailActivity,
                        resources.getText(R.string.deleted_favorite) as String)
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}