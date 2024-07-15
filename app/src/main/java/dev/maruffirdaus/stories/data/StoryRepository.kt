package dev.maruffirdaus.stories.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.data.source.local.room.StoryDatabase
import dev.maruffirdaus.stories.data.source.remote.response.GeneralResponse
import dev.maruffirdaus.stories.data.source.remote.response.GetResponse
import dev.maruffirdaus.stories.data.source.remote.response.LoginResponse
import dev.maruffirdaus.stories.data.source.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    private val generalResult = MediatorLiveData<Result<GeneralResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()
    private val storiesWithLocationResult = MediatorLiveData<Result<GetResponse>>()

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<GeneralResponse>> {
        generalResult.value = Result.Loading
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                if (response.isSuccessful) {
                    generalResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        GeneralResponse::class.java
                    )
                    generalResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                generalResult.value = Result.Error(t.message.toString())
            }
        })

        return generalResult
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        GeneralResponse::class.java
                    )
                    loginResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message.toString())
            }
        })

        return loginResult
    }

    fun getStories(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<GetResponse>> {
        storiesWithLocationResult.value = Result.Loading
        val client = apiService.getStoriesWithLocation(token)
        client.enqueue(object: Callback<GetResponse> {
            override fun onResponse(call: Call<GetResponse>, response: Response<GetResponse>) {
                if (response.isSuccessful) {
                    storiesWithLocationResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        GeneralResponse::class.java
                    )
                    storiesWithLocationResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<GetResponse>, t: Throwable) {
                storiesWithLocationResult.value = Result.Error(t.message.toString())
            }
        })

        return storiesWithLocationResult
    }

    fun sendStory(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody
    ): LiveData<Result<GeneralResponse>> {
        generalResult.value = Result.Loading
        val client = apiService.sendStory(token, file, desc)
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    generalResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        GeneralResponse::class.java
                    )
                    generalResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                generalResult.value = Result.Error(t.message.toString())
            }
        })

        return generalResult
    }

    fun sendStoryWithLocation(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<Result<GeneralResponse>> {
        generalResult.value = Result.Loading
        val client = apiService.sendStoryWithLocation(token, file, desc, lat, lon)
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    generalResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        GeneralResponse::class.java
                    )
                    generalResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                generalResult.value = Result.Error(t.message.toString())
            }
        })

        return generalResult
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}