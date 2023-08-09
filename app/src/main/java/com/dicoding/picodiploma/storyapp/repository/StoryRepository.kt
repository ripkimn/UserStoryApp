package com.dicoding.picodiploma.storyapp.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.dicoding.picodiploma.storyapp.api.ApiService
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.StoryResponse
import com.dicoding.picodiploma.storyapp.data.StoryPagingSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(private val apiService: ApiService) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    fun getStory(token: String) =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData

    fun getPage() {
        RetrofitClient.instance.getStory("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUt0UE1xVEdHN2lEaWpyZlgiLCJpYXQiOjE2ODI0MjEwNjV9.-UyNZLmDmC8oem4twZgKpIz3IFjDvDtxCz6uICTN8pE")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>
                ) {
                    val data = response.body()!!

                    if (!data.error) {
                        Log.d("aaa", data.message)
                    } else {

                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {

                }

            })


    }
}