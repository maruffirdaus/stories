package dev.maruffirdaus.stories.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.stories.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun sendStory(token: String, file: MultipartBody.Part, desc: RequestBody) =
        storyRepository.sendStory(token, file, desc)

    fun sendStoryWithLocation(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ) =
        storyRepository.sendStoryWithLocation(token, file, desc, lat, lon)
}