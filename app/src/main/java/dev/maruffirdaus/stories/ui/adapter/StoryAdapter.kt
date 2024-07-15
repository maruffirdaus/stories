package dev.maruffirdaus.stories.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.databinding.ItemRowStoryBinding
import dev.maruffirdaus.stories.helper.DateHelper
import dev.maruffirdaus.stories.ui.main.StoryActivity

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    inner class StoryViewHolder(
        private val context: Context,
        private val binding: ItemRowStoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            with(binding) {
                tvItemName.text = story.name
                Glide.with(context)
                    .load(story.photoUrl)
                    .into(binding.ivItemPhoto)
                date.text = DateHelper.covertDate(story.createdAt)
                description.text = story.description

                main.setOnClickListener {
                    val optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            it.context as Activity,
                            Pair(ivItemPhoto, "photo")
                        )
                    val intent = Intent(it.context, StoryActivity::class.java)
                    intent.putExtra(StoryActivity.EXTRA_STORY, story)
                    it.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}