package dev.maruffirdaus.stories.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.StoryRepository
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import kotlinx.coroutines.launch

class AuthViewModel(private val loginPref: LoginPreferences, private val storyRepository: StoryRepository) : ViewModel() {
    fun saveLoginResult(loginResult: LoginResult) {
        viewModelScope.launch {
            loginPref.saveLoginResult(loginResult)
        }
    }

    fun register(name: String, email: String, password: String) =
        storyRepository.register(name, email, password)

    fun login(email: String, password: String) = storyRepository.login(email, password)
}