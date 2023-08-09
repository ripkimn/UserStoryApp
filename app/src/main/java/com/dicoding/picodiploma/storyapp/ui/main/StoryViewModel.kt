package com.dicoding.picodiploma.storyapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyapp.repository.AuthRepositoryInterface
import com.dicoding.picodiploma.storyapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    repository: StoryRepository,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    private val currentToken = MutableLiveData(getToken())

    val story = currentToken.switchMap { tokenString ->
        repository.getStory(tokenString.toString()).cachedIn(viewModelScope)
    }

    fun setToken(token: String) {
        currentToken.value = token
    }

    private fun getToken(): String? {
        return authRepository.getToken()
    }

    companion object {
        private const val DEFAULT_TOKEN = ""
    }
}