package com.dicoding.picodiploma.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.repository.MapsRepository

@Suppress("UNCHECKED_CAST")
class MapsViewModelFactory(private val mapsRepository: MapsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(mapsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}