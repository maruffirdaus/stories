package dev.maruffirdaus.stories.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Repository
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import kotlinx.coroutines.launch

class MainViewModel(private val loginPref: LoginPreferences, private val repository: Repository) :
    ViewModel() {
    var listStory: LiveData<Result<List<StoryEntity>>> = MutableLiveData()

    fun getLoginResult(): LiveData<Set<String>?> {
        return loginPref.getLoginResult().asLiveData()
    }

    fun clearLoginResult() {
        viewModelScope.launch {
            loginPref.clearLoginResult()
        }
    }

    fun getStories(token: String) {
        listStory = repository.getStories(token)
    }
}