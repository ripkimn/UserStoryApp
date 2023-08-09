package com.dicoding.picodiploma.storyapp.api

import com.dicoding.picodiploma.storyapp.api.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("v1/stories")
    fun getStory(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @GET("v1/stories/{id}")
    fun getDetail(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<DetailResponse>

    @Multipart
    @POST("/v1/stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignInResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @GET("/v1/stories?location=1")
    fun getLocation(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>

    @GET("/v1/stories")
    suspend fun getPage(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : StoryResponse
}