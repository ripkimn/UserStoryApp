package com.dicoding.picodiploma.storyapp.ui.main

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.repository.StoryRepository

class MainViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getPage(token: String) {
        return storyRepository.getPage()
    }
}