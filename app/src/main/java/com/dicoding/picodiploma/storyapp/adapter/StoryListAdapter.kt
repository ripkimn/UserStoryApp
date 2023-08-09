package com.dicoding.picodiploma.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.api.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.databinding.ItemStoryBinding
import com.dicoding.picodiploma.storyapp.ui.detail.DetailActivity

class StoryListAdapter : PagingDataAdapter<ListStoryItem, StoryListAdapter.StoryViewHolder>(STORY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(ivCardStory)

                tvCardName.text = story.name

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val STORY_COMPARATOR = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}