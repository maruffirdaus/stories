package dev.maruffirdaus.stories

import android.app.Application
import com.google.android.material.color.DynamicColors

class StoriesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}