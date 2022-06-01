package com.wahyuhw.userstoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.databinding.ItemStoryBinding
import com.wahyuhw.userstoryapp.ui.activity.DetailActivity

class ListStoryAdapter:
    PagingDataAdapter<StoryItem, ListStoryAdapter.ListStoryViewHolder>(StoryComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ListStoryViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: StoryItem) {
            with(binding) {
                Glide.with(itemView.context).load(storyItem.photoUrl).into(ivPicture)
                tvDescription.text = storyItem.description

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_STORY, storyItem)
                    intent.putExtra(DetailActivity.EXTRA_TYPE, DetailActivity.STORY)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivPicture, "image"),
                            Pair(tvDescription, "desc"))
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    class StoryComparator : DiffUtil.ItemCallback<StoryItem>() {
        override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            return oldItem.description == newItem.description
        }
    }
}