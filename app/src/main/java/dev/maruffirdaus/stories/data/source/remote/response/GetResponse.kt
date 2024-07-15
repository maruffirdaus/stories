package dev.maruffirdaus.stories.data.source.remote.response

import com.google.gson.annotations.SerializedName
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity

data class GetResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("listStory")
	val listStory: List<StoryEntity> = emptyList()
)
