package dev.maruffirdaus.stories.ui.main

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.source.local.preferences.dataStore
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import dev.maruffirdaus.stories.databinding.FragmentNearbyBinding
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class NearbyFragment : Fragment() {
    private lateinit var binding: FragmentNearbyBinding
    private var loginResult: LoginResult? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        setMapStyle()
        getMyLocation()

        viewModel.getStoriesWithLocation("Bearer " + (loginResult?.token ?: "token"))
            .observe(viewLifecycleOwner) {
                if (it is Result.Success) {
                    it.data.listStory.forEach { data ->
                        if (data.lat != null && data.lon != null) {
                            val latLng = LatLng(data.lat, data.lon)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(data.name)
                                    .snippet(data.description)
                            )
                        }
                    }
                } else if (it is Result.Error) {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(getString(R.string.error))
                        .setMessage(it.error)
                        .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
    }

    private fun setMapStyle() {
        val darkModeFlag =
            context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        val isDarkMode = darkModeFlag == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style_dark)
            )
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
                requireActivity().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLoginResult()
        obtainViewModel()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getLoginResult() {
        val loginPref = LoginPreferences.getInstance(requireActivity().dataStore)
        val loginResultSet = runBlocking { loginPref.getLoginResult().first() }
        if (loginResultSet != null) {
            loginResult = LoginResult(
                loginResultSet.elementAt(0),
                loginResultSet.elementAt(1),
                loginResultSet.elementAt(2)
            )
        }
    }

    private fun obtainViewModel() {
        val factory =
            ViewModelFactory.getInstance(requireActivity().application, requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }
}