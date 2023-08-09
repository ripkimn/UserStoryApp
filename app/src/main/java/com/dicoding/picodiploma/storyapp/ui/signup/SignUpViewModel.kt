package com.dicoding.picodiploma.storyapp.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.repository.AuthRepository

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun signUp(name: String, email: String, password: String) {
        authRepository.register(name, email, password)
    }

    fun getSignUpResult(): LiveData<Boolean> {
        return authRepository.signUpResult
    }
}