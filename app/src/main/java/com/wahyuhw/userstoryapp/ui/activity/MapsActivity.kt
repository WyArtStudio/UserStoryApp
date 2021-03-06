package com.wahyuhw.userstoryapp.ui.activity

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.wahyuhw.userstoryapp.R
import com.wahyuhw.userstoryapp.data.network.ResponseCallback
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.databinding.ActivityMapsBinding
import com.wahyuhw.userstoryapp.utils.showShortToast
import com.wahyuhw.userstoryapp.utils.vectorToBitmap
import com.wahyuhw.userstoryapp.viewmodel.MainViewModel
import com.wahyuhw.userstoryapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, ResponseCallback<List<StoryItem>> {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressed() }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadData()

        binding.menuButton.setOnClickListener {
            val popupMenu = PopupMenu(this@MapsActivity, binding.menuButton)
            popupMenu.menuInflater.inflate(R.menu.pop_menu_maps, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.order) {
                    1 -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    2 -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    3 -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    4 -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                }

                false
            }
            
            popupMenu.show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        // Maps Configuration
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getListLocatedStory().observe(this@MapsActivity) {
                when (it) {
                    is ResponseResource.Loading -> onLoading()
                    is ResponseResource.Success -> it.data?.listStory?.let { it1 -> onSuccess(it1) }
                    is ResponseResource.Error -> onFailed(it.message)
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onSuccess(data: List<StoryItem>) {
        binding.progressBar.visibility = gone
        if (data.isNotEmpty()) {
            for ((position, story) in data.withIndex()) {
                val coordinates = LatLng(story.lat as Double, story.lon as Double)
                val title = story.description
                // Marker Options
                mMap.addMarker(MarkerOptions()
                    .position(coordinates)
                    .title(title)
                    .icon(vectorToBitmap(R.drawable.ic_marker,
                        ContextCompat.getColor(this@MapsActivity, R.color.colorBlueSky), resources)))
                if (position == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
                }
            }
        }
    }

    override fun onLoading() {
        binding.progressBar.visibility = visible
    }

    override fun onFailed(message: String?) {
        binding.progressBar.visibility = gone
        if (message != null) {
            showShortToast(this@MapsActivity, message)
        }
    }
}