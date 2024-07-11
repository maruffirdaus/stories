package dev.maruffirdaus.stories.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Repository
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import kotlinx.coroutines.launch

class AuthViewModel(private val loginPref: LoginPreferences, private val repository: Repository) : ViewModel() {
    fun saveLoginResult(loginResult: LoginResult) {
        viewModelScope.launch {
            loginPref.saveLoginResult(loginResult)
        }
    }

    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)

    fun login(email: String, password: String) = repository.login(email, password)
}