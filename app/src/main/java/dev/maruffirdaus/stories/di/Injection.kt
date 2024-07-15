package dev.maruffirdaus.stories.di

import android.content.Context
import dev.maruffirdaus.stories.data.StoryRepository
import dev.maruffirdaus.stories.data.source.local.room.StoryDatabase
import dev.maruffirdaus.stories.data.source.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)

        return StoryRepository.getInstance(database, apiService)
    }
}