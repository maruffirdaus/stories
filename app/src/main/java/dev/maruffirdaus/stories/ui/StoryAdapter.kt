package dev.maruffirdaus.stories.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.databinding.ItemRowStoryBinding
import dev.maruffirdaus.stories.helper.DateHelper
import dev.maruffirdaus.stories.helper.StoryDiffCallback
import dev.maruffirdaus.stories.ui.main.StoryActivity

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val listStory = ArrayList<StoryEntity>()

    fun setListStory(listStory: List<StoryEntity>) {
        val diffCallback = StoryDiffCallback(this.listStory, listStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listStory.clear()
        this.listStory.addAll(listStory)
        diffResult.dispatchUpdatesTo(this)
    }

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
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}