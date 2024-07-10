package dev.maruffirdaus.stories.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Repository
import dev.maruffirdaus.stories.data.dataStore
import dev.maruffirdaus.stories.di.Injection

class ViewModelFactory private constructor(
    private val loginPref: LoginPreferences,
    private val repository: Repository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(loginPref, repository) as T
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