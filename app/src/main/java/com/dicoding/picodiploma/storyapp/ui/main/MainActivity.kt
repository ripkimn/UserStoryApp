package com.dicoding.picodiploma.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.adapter.StoryListAdapter
import com.dicoding.picodiploma.storyapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyapp.helper.StoryViewModelFactory
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.repository.MockAuthRepository
import com.dicoding.picodiploma.storyapp.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.picodiploma.storyapp.ui.maps.MapsActivity
import com.dicoding.picodiploma.storyapp.ui.signin.SignInActivity
import com.dicoding.picodiploma.storyapp.ui.signin.SignInViewModel
import kotlin.math.sign

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(this)
        val signInViewModelFactory = ViewModelFactory(authRepository)
        signInViewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]


        if (signInViewModel.getToken(this).isNullOrEmpty()) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        } else {
            getStory()
        }

        val layoutManager =LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        addStory()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (signInViewModel.getToken(this).isNullOrEmpty()){
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps_menu -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.logout_menu -> {
                showLogoutDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addStory(){
        binding.fabAddStory.setOnClickListener{
            val intent = Intent (this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.alert_logout))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                signInViewModel.logout()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .setNegativeButton(getString(R.string.tidak), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (signInViewModel.getToken(this).isNullOrEmpty()) {
            finish()
        } else if (!signInViewModel.getToken(this).isNullOrEmpty()){
            getStory()
        }
    }

    private fun getStory() {
        val storyRepository =  StoryRepository(RetrofitClient.instance)
        val authRepository = MockAuthRepository()

        val storyViewModelFactory = StoryViewModelFactory(storyRepository, authRepository)
        storyViewModel = ViewModelProvider(this, storyViewModelFactory)[StoryViewModel::class.java]

        storyViewModel.setToken("Bearer " + signInViewModel.getToken(this).toString())
        val adapter = StoryListAdapter()

        binding.apply {
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }
        storyViewModel.story.observe(this) {
            adapter.submitData(this.lifecycle, it)
            Log.e("token", signInViewModel.getToken(this).toString())
        }
    }
}