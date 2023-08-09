package com.dicoding.picodiploma.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.SignUpResponse
import com.dicoding.picodiploma.storyapp.databinding.ActivitySignUpBinding
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.ui.main.MainActivity
import com.dicoding.picodiploma.storyapp.ui.signin.SignInActivity
import com.dicoding.picodiploma.storyapp.ui.signin.SignInViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val authRepository = AuthRepository(this)
        val signUpViewModelFactory = ViewModelFactory(authRepository)
        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory)[SignUpViewModel::class.java]

        binding.btnSignup.isEnabled = false
        binding.loadingBar.visibility = View.INVISIBLE
        binding.btnSignup.visibility = View.VISIBLE

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.btnSignup.setOnClickListener { signUpAct() }

        binding.tvSignin2.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        setupAnimation()
    }

    private fun setupAnimation() {
        val edtName = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(500)
        val edtLayout = ObjectAnimator.ofFloat(binding.edtLayout, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)
        val tvSignin1 = ObjectAnimator.ofFloat(binding.tvSignin1, View.ALPHA, 1f).setDuration(500)
        val tvSignin2 = ObjectAnimator.ofFloat(binding.tvSignin2, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(edtLayout, edtPassword)
        }


        AnimatorSet().apply {
            playSequentially(edtName, edtEmail, together, btnSignUp, tvSignin1, tvSignin2)
            startDelay -= 500
        }.start()
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
        binding.btnSignup.isEnabled = password.toString().length >= 8
    }

    private fun signUpAct() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.btnSignup.visibility = View.INVISIBLE

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        signUpViewModel.signUp(name, email, password)
        signUpViewModel.getSignUpResult().observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Daftar berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {
                binding.loadingBar.visibility = View.INVISIBLE
                binding.btnSignup.visibility = View.VISIBLE
                Toast.makeText(this, "Daftar gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }

}