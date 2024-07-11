package dev.maruffirdaus.stories.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.stories.data.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewStoryViewModel(private val repository: Repository) : ViewModel() {
    fun sendStory(token: String, file: MultipartBody.Part, desc: RequestBody) =
        repository.sendStory(token, file, desc)
}