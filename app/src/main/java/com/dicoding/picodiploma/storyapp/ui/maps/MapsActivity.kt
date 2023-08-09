package com.dicoding.picodiploma.storyapp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.api.RetrofitClient
import com.dicoding.picodiploma.storyapp.api.response.StoryResponse
import com.dicoding.picodiploma.storyapp.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.storyapp.repository.AuthRepository
import com.dicoding.picodiploma.storyapp.repository.MapsRepository
import com.dicoding.picodiploma.storyapp.helper.ViewModelFactory
import com.dicoding.picodiploma.storyapp.ui.signin.SignInViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var mapViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(this)
        val mapsRepository = MapsRepository(authRepository, this)
        val authViewModelFactory = ViewModelFactory(authRepository)
        val mapsViewModelFactory = MapsViewModelFactory(mapsRepository)

        signInViewModel = ViewModelProvider(this, authViewModelFactory)[SignInViewModel::class.java]
        mapViewModel = ViewModelProvider(this, mapsViewModelFactory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        getLatLngValues()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun getLatLngValues() {
        val token = signInViewModel.getToken(this)

        RetrofitClient.instance.getLocation(
            "Bearer $token"
        ).enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                val storyItemResponse = response.body()!!
                val listStory = storyItemResponse.listStory

                if (!storyItemResponse.error && listStory.isNotEmpty()) {
                    val latLngList = mutableListOf<LatLng>()

                    for (storyItem in listStory) {
                        val lat = storyItem.lat
                        val lng = storyItem.lon

                        val latLng = LatLng(lat as Double, lng as Double)
                        latLngList.add(latLng)

                        val title = storyItem.name

                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(title)
                        )
                    }
                    val builder = LatLngBounds.Builder()

                    for (coordinate in latLngList) {
                        builder.include(coordinate)
                    }

                    val bounds = builder.build()
                    val padding = 100

                    if (latLngList.isNotEmpty()) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                    }
                }

            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("error", "onFailure")
            }

        })
    }
}