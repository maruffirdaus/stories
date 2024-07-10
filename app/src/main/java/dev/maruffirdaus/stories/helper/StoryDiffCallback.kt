package dev.maruffirdaus.stories.helper

import androidx.recyclerview.widget.DiffUtil
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity

class StoryDiffCallback(
    private val oldStoryList: List<StoryEntity>,
    private val newStoryList: List<StoryEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldStoryList.size
    override fun getNewListSize(): Int = newStoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldStoryList[oldItemPosition].id == newStoryList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = oldStoryList[oldItemPosition]
        val newStory = newStoryList[newItemPosition]
        val name = oldStory.name == newStory.name
        val description = oldStory.description == newStory.description
        val photoUrl = oldStory.photoUrl == newStory.photoUrl
        val createdAt = oldStory.createdAt == newStory.createdAt
        val lat = oldStory.lat == newStory.lat
        val lon = oldStory.lon == newStory.lon

        return name && description && photoUrl && createdAt && lat && lon
    }
}