package com.plb.conference.repositories

import com.plb.conference.domain.models.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

object NetworkModule {
    val BASE_URL = "https://faux-api.com/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApi: UserApi = retrofit.create(UserApi::class.java)

}

//====================
interface UserApi {
    @GET("user_8795252158346524/")
    suspend fun getUsers(): UserResponse

    @GET("meetings_8795252158346524/")
    suspend fun getMeetings(): MeetingResponse

}

data class UserResponse(
    val status: String,
    val result: List<User>
)

data class MeetingResponse (
    val status: String,
    val result: List<Meeting>
)

data class Meeting (
    val id: Long,
    val user1: String,
    val user2: String,
    val room: String,
    val date: String
)

//===================