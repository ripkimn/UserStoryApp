package com.dicoding.picodiploma.storyapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.SignInResponse
import com.dicoding.picodiploma.storyapp.api.response.SignUpResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthRepository(context: Context) : AuthRepository {
        return AuthRepository(context)
    }
}

class AuthRepository @Inject constructor(private val context: Context) {

    private val _signInResult = MutableLiveData<Boolean>()
    val signInResult: LiveData<Boolean> = _signInResult

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> = _signUpResult


    fun login(email: String, password: String) {
        RetrofitClient.instance.signIn(
            email, password
        ).enqueue(object: Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                val user = response.body()
                if (user?.error == false) {
                    val token = user.loginResult.token
                    token?.let {
                        saveToken(it)
                        _signInResult.value = true
                        return
                    }
                }
                _signInResult.value = false
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                Log.e("error", t.message.toString())
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        RetrofitClient.instance.signUp(
            name, email, password
        ).enqueue(object: Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>
            ) {
                val user = response.body()
                _signUpResult.value = user?.error == false
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.e("error", t.message.toString())
            }

        })
    }

    fun logout() {
        clearToken()
    }

    private fun saveToken(token: String) {
        val sharedPref = context.getSharedPreferences(R.string.pref_key.toString(), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("TOKEN_KEY", token)
        editor.apply()
    }

    fun getToken(): String? {
        val sharedPref = this.context.getSharedPreferences(R.string.pref_key.toString(), Context.MODE_PRIVATE)
        return sharedPref.getString("TOKEN_KEY", null)
    }

    fun clearToken() {
        val sharedPref = context.getSharedPreferences(R.string.pref_key.toString(), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("TOKEN_KEY")
        editor.apply()
    }
}