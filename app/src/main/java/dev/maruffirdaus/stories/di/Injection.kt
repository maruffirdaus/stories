package dev.maruffirdaus.stories.di

import android.content.Context
import dev.maruffirdaus.stories.data.Repository
import dev.maruffirdaus.stories.data.source.local.room.StoryDatabase
import dev.maruffirdaus.stories.data.source.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()

        return Repository.getInstance(apiService, dao)
    }
}