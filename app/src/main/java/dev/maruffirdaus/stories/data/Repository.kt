package dev.maruffirdaus.stories.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.data.source.local.room.StoryDao
import dev.maruffirdaus.stories.data.source.remote.response.SendResponse
import dev.maruffirdaus.stories.data.source.remote.response.ErrorResponse
import dev.maruffirdaus.stories.data.source.remote.response.LoginResponse
import dev.maruffirdaus.stories.data.source.remote.response.RegisterResponse
import dev.maruffirdaus.stories.data.source.remote.response.StoryResponse
import dev.maruffirdaus.stories.data.source.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class Repository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao
) {
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()
    private val storyResult = MediatorLiveData<Result<List<StoryEntity>>>()
    private val sendResult = MediatorLiveData<Result<SendResponse>>()

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    registerResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        ErrorResponse::class.java
                    )
                    registerResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message.toString())
            }
        })

        return registerResult
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
                        ErrorResponse::class.java
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

    fun getStories(token: String): LiveData<Result<List<StoryEntity>>> {
        storyResult.value = Result.Loading
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                    val listStory = ArrayList<StoryEntity>()
                    Executors.newSingleThreadExecutor().execute {
                        stories?.forEach {
                            val story = StoryEntity(
                                it!!.id,
                                it.name,
                                it.description,
                                it.photoUrl,
                                it.createdAt,
                                it.lat,
                                it.lon
                            )
                            listStory.add(story)
                        }
                        storyDao.deleteAll()
                        storyDao.insertStories(listStory)
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                storyResult.value = Result.Error(t.message.toString())
            }
        })

        val localData = storyDao.getStories()
        storyResult.addSource(localData) { newData: List<StoryEntity> ->
            storyResult.value = Result.Success(newData)
        }

        return storyResult
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(apiService: ApiService, storyDao: StoryDao): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, storyDao)
            }.also { instance = it }
    }

    fun sendStory(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody
    ): LiveData<Result<SendResponse>> {
        sendResult.value = Result.Loading
        val client = apiService.sendStory(token, file, desc)
        client.enqueue(object : Callback<SendResponse> {
            override fun onResponse(call: Call<SendResponse>, response: Response<SendResponse>) {
                if (response.isSuccessful) {
                    sendResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(
                        response.errorBody()!!.string(),
                        ErrorResponse::class.java
                    )
                    sendResult.value = Result.Error(errorBody.message)
                }
            }

            override fun onFailure(call: Call<SendResponse>, t: Throwable) {
                sendResult.value = Result.Error(t.message.toString())
            }
        })

        return sendResult
    }
}