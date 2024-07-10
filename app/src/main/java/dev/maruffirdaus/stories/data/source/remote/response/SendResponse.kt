package dev.maruffirdaus.stories.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SendResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
