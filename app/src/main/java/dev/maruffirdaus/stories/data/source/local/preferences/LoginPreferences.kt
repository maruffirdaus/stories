package dev.maruffirdaus.stories.data.source.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val loginKey = stringSetPreferencesKey("login")

    fun getLoginResult(): Flow<Set<String>?> {
        return dataStore.data.map {
            it[loginKey]
        }
    }

    suspend fun saveLoginResult(loginResult: LoginResult) {
        dataStore.edit {
            it[loginKey] = setOf(loginResult.userId, loginResult.name, loginResult.token)
        }
    }

    suspend fun clearLoginResult() {
        dataStore.edit {
            it.remove(loginKey)
        }
    }

    companion object {
        @Volatile
        private var instance: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences =
            instance ?: synchronized(this) {
                instance ?: LoginPreferences(dataStore)
            }.also { instance = it }
    }
}