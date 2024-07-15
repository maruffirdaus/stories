package dev.maruffirdaus.stories.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.StoryRepository
import dev.maruffirdaus.stories.data.source.local.preferences.dataStore
import dev.maruffirdaus.stories.di.Injection
import dev.maruffirdaus.stories.ui.auth.viewmodel.AuthViewModel
import dev.maruffirdaus.stories.ui.main.viewmodel.MainViewModel
import dev.maruffirdaus.stories.ui.main.viewmodel.NewStoryViewModel

class ViewModelFactory private constructor(
    private val loginPref: LoginPreferences,
    private val storyRepository: StoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(loginPref, storyRepository) as T
        }else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(loginPref, storyRepository) as T
        } else if (modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
            return NewStoryViewModel(storyRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application, context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    LoginPreferences.getInstance(application.dataStore),
                    Injection.provideRepository(context)
                )
            }.also { instance = it }
    }
}