package com.dicoding.picodiploma.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.repository.MapsRepository
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(private val mapsRepository: MapsRepository) : ViewModel() {
    private val _latLngList: LiveData<List<LatLng>> = mapsRepository.latLngList
    val latLngList: LiveData<List<LatLng>>
        get() = _latLngList

    init {
        observeLatLngList()
    }

    private fun observeLatLngList() {
        _latLngList.observeForever {latLngList ->
            Log.e("MapViewModel", "LatLngList : $latLngList")

        }
    }
}