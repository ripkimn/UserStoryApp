package com.dicoding.picodiploma.storyapp.ui.detail

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.DetailResponse
import com.dicoding.picodiploma.storyapp.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.storyapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.ui.signin.SignInViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var signInViewModel: SignInViewModel

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(this)
        val signInViewModelFactory = ViewModelFactory(authRepository)
        signInViewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

        val id = intent.getStringExtra(EXTRA_ID)

        RetrofitClient.instance.getDetail(
            id.toString(),
            "Bearer " + signInViewModel.getToken(this)
        ).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                val data = response.body()

                if (data != null) {
                    if (!data.error) {
                        binding.tvName.text = data.story.name
                        Glide.with(this@DetailActivity)
                            .load(data.story.photoUrl)
                            .centerCrop()
                            .into(binding.ivStory)
                        binding.tvDescription.text = data.story.description
                    }
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {

            }
        })

        binding.tvName.text
    }
}