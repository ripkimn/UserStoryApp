package com.dicoding.picodiploma.storyapp.ui.signin

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.repository.AuthRepository

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun signIn(email: String, password: String) {
        authRepository.login(email, password)
    }

    fun logout() {
        return authRepository.logout()
    }

    fun getSignInResult(): LiveData<Boolean> {
        return authRepository.signInResult
    }

    fun getToken(context: Context): String? {
        return authRepository.getToken()
    }
}