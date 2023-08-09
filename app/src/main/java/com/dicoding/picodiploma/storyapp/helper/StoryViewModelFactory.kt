package com.dicoding.picodiploma.storyapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.repository.AuthRepositoryInterface
import com.dicoding.picodiploma.storyapp.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.ui.main.StoryViewModel

@Suppress("UNCHECKED_CAST")
class StoryViewModelFactory(private val repository: StoryRepository, private val authRepository: AuthRepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}