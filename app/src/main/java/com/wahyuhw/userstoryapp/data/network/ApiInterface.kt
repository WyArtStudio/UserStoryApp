package com.wahyuhw.userstoryapp.data.network

import com.wahyuhw.userstoryapp.data.response.LoginResponse
import com.wahyuhw.userstoryapp.data.response.ApiResponse
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("login")
    fun loginUser(@Body requestBody: RequestBody): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body requestBody: RequestBody): Call<ApiResponse>

    @POST("stories")
    @Multipart
    @JvmSuppressWildcards
    fun addStory(
        @Header("Authorization") token: String,
        @PartMap partMap: Map<String, RequestBody>,
        @Part file: MultipartBody.Part,
    ): Call<ApiResponse>

    // TODO: Check return type
    @GET("stories")
    fun getStory(
        @Header("Authorization") token: String
    ): List<StoryItem>

    @GET("stories?location=1")
    fun getLocatedStory(
        @Header("Authorization") token: String
    ): Call<StoryResponse>
}