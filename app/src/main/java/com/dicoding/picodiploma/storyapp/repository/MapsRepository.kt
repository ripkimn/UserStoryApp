package com.dicoding.picodiploma.storyapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.api.response.StoryResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsRepository(
    private val authRepository: AuthRepository,
    private val context: Context
) {
    private val _latLngList = MutableLiveData<List<LatLng>>()
    val latLngList: LiveData<List<LatLng>> = _latLngList

    fun getLatLngValues() {
        val token = authRepository.getToken()

        if (!token.isNullOrEmpty()) {
            RetrofitClient.instance.getLocation(
                "Bearer $token"
            ).enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>
                ) {
                    val storyItemResponse = response.body()!!
                    val listStory = storyItemResponse.listStory

                    if (!storyItemResponse.error) {
                        val latLngList = mutableListOf<LatLng>()

                        for (storyItem in listStory) {
                            val lat = storyItem.lat
                            val lng = storyItem.lon

                            val latLng = LatLng(lat as Double, lng as Double)
                            latLngList.add(latLng)
                        }

                        _latLngList.value = latLngList
                    } else {
                        _latLngList.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e("error", "onFailure")
                }

            })
        } else {
            _latLngList.value = emptyList()
        }
    }

    fun getStoryItemByLatLng(latLng: LatLng): ListStoryItem? {
        val token = authRepository.getToken()

        if (!token.isNullOrEmpty()) {
            val storyItemResponse = RetrofitClient.instance.getLocation(
                "Bearer $token"
            ).execute().body()
            val listStory = storyItemResponse?.listStory

            if (!storyItemResponse?.error!! && !listStory.isNullOrEmpty()) {
                return listStory.find {
                    it.lat == latLng.latitude && it.lon == latLng.longitude
                }
            }
        }
        return null
    }
}