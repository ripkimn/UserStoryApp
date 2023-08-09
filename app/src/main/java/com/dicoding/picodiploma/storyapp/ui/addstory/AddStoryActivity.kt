package com.dicoding.picodiploma.storyapp.ui.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.AddStoryResponse
import com.dicoding.picodiploma.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.ui.camera.CameraActivity
import com.dicoding.picodiploma.storyapp.ui.signin.SignInViewModel
import com.dicoding.picodiploma.storyapp.utils.reduceFileImage
import com.dicoding.picodiploma.storyapp.utils.rotateFile
import com.dicoding.picodiploma.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var signInViewModel: SignInViewModel
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(this)
        val signInViewModelFactory = ViewModelFactory(authRepository)
        signInViewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnPost.setOnClickListener{ postStory() }

        binding.loadingBar.visibility = View.INVISIBLE
        binding.btnPost.visibility = View.VISIBLE
    }

    private fun postStory() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.btnPost.visibility = View.INVISIBLE

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            RetrofitClient.instance.addStory(
                "Bearer " + signInViewModel.getToken(this),
                imageMultipart,
                description
            ).enqueue(object: Callback<AddStoryResponse> {
                override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(this@AddStoryActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@AddStoryActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    Toast.makeText(this@AddStoryActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            Toast.makeText(this@AddStoryActivity, "Please choose image first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val choosePhoto = getString(R.string.choose_photo)
        val chooser = Intent.createChooser(intent, choosePhoto)
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this@AddStoryActivity)
            getFile = myFile

            binding.ivPrev.setImageURI(selectedImage)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(CameraActivity.PICTURE) as File
            val isBackCamera = it.data?.getBooleanExtra(CameraActivity.IS_BACK_CAMERA, false) as Boolean

            getFile = myFile

            myFile.let { file ->
                rotateFile(file, isBackCamera)
                val result = BitmapFactory.decodeFile(file.path)
                binding.ivPrev.setImageBitmap(result)
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}