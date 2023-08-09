package com.dicoding.picodiploma.storyapp.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.SignInResponse
import com.dicoding.picodiploma.storyapp.databinding.ActivitySignInBinding
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.ui.main.MainActivity
import com.dicoding.picodiploma.storyapp.ui.signup.SignUpActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val authRepository = AuthRepository(this)
        val signInViewModelFactory = ViewModelFactory(authRepository)
        signInViewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

        binding.btnSignin.isEnabled = false
        binding.loadingBar.visibility = View.INVISIBLE
        binding.btnSignin.visibility = View.VISIBLE

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        setupAnimation()

        binding.btnSignin.setOnClickListener { signInAct() }

        binding.tvSignup2.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setEnable() {
        val password = binding.edtPassword.text
        binding.btnSignin.isEnabled = password.toString().length >= 8
    }


    private fun signInAct() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.btnSignin.visibility = View.INVISIBLE

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        signInViewModel.signIn(email, password)
        signInViewModel.getSignInResult().observe(this, Observer {isSuccess ->
            if (isSuccess) {
                val token = signInViewModel.getToken(this)
                if (!token.isNullOrEmpty()) {
                    Toast.makeText(this, "Masuk berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Masuk gagal", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun setupAnimation() {
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtLayout = ObjectAnimator.ofFloat(binding.edtLayout, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(500)
        val tvSignup1 = ObjectAnimator.ofFloat(binding.tvSignup1, View.ALPHA, 1f).setDuration(500)
        val tvSignup2 = ObjectAnimator.ofFloat(binding.tvSignup2, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(edtLayout, edtPassword)
        }

        AnimatorSet().apply {
            playSequentially(edtEmail,together, btnSignIn, tvSignup1, tvSignup2)
            startDelay -= 500
        }.start()
    }
}