package dev.maruffirdaus.stories.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val loginPref: LoginPreferences,
    private val storyRepository: StoryRepository
) :
    ViewModel() {
    fun getLoginResult(): LiveData<Set<String>?> {
        return loginPref.getLoginResult().asLiveData()
    }

    fun clearLoginResult() {
        viewModelScope.launch {
            loginPref.clearLoginResult()
        }
    }

    fun getStories(token: String) = storyRepository.getStories(token).cachedIn(viewModelScope)

    fun getStoriesWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)
}