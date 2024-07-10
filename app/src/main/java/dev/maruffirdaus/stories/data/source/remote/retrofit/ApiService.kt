package dev.maruffirdaus.stories.data.source.remote.retrofit

import dev.maruffirdaus.stories.data.source.remote.response.LoginResponse
import dev.maruffirdaus.stories.data.source.remote.response.RegisterResponse
import dev.maruffirdaus.stories.data.source.remote.response.SendResponse
import dev.maruffirdaus.stories.data.source.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun sendStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") desc: RequestBody,
    ): Call<SendResponse>
}