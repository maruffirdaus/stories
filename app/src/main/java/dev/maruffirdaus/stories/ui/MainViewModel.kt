package dev.maruffirdaus.stories.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Repository
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val loginPref: LoginPreferences, private val repository: Repository) :
    ViewModel() {
    var listStory: LiveData<Result<List<StoryEntity>>> = MutableLiveData()

    fun getLoginResult(): LiveData<Set<String>?> {
        return loginPref.getLoginResult().asLiveData()
    }

    fun saveLoginResult(loginResult: LoginResult) {
        viewModelScope.launch {
            loginPref.saveLoginResult(loginResult)
        }
    }

    fun clearLoginResult() {
        viewModelScope.launch {
            loginPref.clearLoginResult()
        }
    }

    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)

    fun login(email: String, password: String) = repository.login(email, password)

    fun getStories(token: String) {
        listStory = repository.getStories(token)
    }

    fun sendStory(token: String, file: MultipartBody.Part, desc: RequestBody) =
        repository.sendStory(token, file, desc)
}