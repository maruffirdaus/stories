package dev.maruffirdaus.stories.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.source.local.preferences.dataStore
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import dev.maruffirdaus.stories.databinding.ActivityNewStoryBinding
import dev.maruffirdaus.stories.helper.reduceFileImage
import dev.maruffirdaus.stories.helper.uriToFile
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.main.viewmodel.NewStoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private var imageUri: String? = null
    private var loginResult: LoginResult? = null
    private lateinit var viewModel: NewStoryViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setupLoadingScreen()
        getExtra()
        getLoginResult()
        obtainViewModel()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setToolbarMenuItemClick()
        setupView()
        setLocationSwitchListener()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hideLoadingScreen()
            if (!isGranted) {
                showPermissionDeniedSnackbar()
                binding.locationSwitch.isChecked = false
            }
        }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            if (0 < ime.bottom) {
                v.updatePadding(
                    left = systemBars.left,
                    right = systemBars.right,
                    bottom = ime.bottom
                )
            } else {
                v.updatePadding(
                    left = systemBars.left,
                    right = systemBars.right,
                    bottom = systemBars.bottom
                )
            }

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.loadingScreen) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLoadingScreen() {
        binding.loadingScreen.setOnTouchListener { _, _ -> true }
    }

    private fun getExtra() {
        imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
    }

    private fun getLoginResult() {
        val loginPref = LoginPreferences.getInstance(dataStore)
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
        val factory = ViewModelFactory.getInstance(application, this)
        viewModel = ViewModelProvider(this, factory)[NewStoryViewModel::class.java]
    }

    private fun setToolbarMenuItemClick() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                @Suppress("DEPRECATION")
                onBackPressed()
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.button_add -> {
                        val desc = edAddDescription.editText?.text ?: ""
                        if (desc.isNotEmpty()) {
                            showLoadingScreen()
                            sendStory(desc.toString(), locationSwitch.isChecked)
                        } else {
                            edAddDescription.error = getString(R.string.can_not_be_empty)
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun sendStory(
        desc: String,
        isLocationIncluded: Boolean = false,
    ) {
        if (imageUri != null) {
            lifecycleScope.launch {
                val token = "Bearer " + (loginResult?.token ?: "token")
                val multipartBody: MultipartBody.Part
                val descRequestBody = desc.toRequestBody("text/plain".toMediaType())

                withContext(Dispatchers.Default) {
                    val imageFile =
                        uriToFile(imageUri!!.toUri(), this@NewStoryActivity).reduceFileImage()
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )
                }

                if (isLocationIncluded) {
                    sendStoryWithLocation(token, multipartBody, descRequestBody)
                } else {
                    observeSendStory(token, multipartBody, descRequestBody)
                }
            }
        }
    }

    private fun sendStoryWithLocation(
        token: String,
        multipartBody: MultipartBody.Part,
        descRequestBody: RequestBody
    ) {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { loc ->
                if (loc.isSuccessful) {
                    val location = loc.result
                    val latRequestBody =
                        location.latitude.toString().toRequestBody("text/plain".toMediaType())
                    val lonRequestBody =
                        location.longitude.toString().toRequestBody("text/plain".toMediaType())
                    observeSendStoryWithLocation(
                        token,
                        multipartBody,
                        descRequestBody,
                        latRequestBody,
                        lonRequestBody
                    )
                }
            }
        } else {
            hideLoadingScreen()
            showPermissionDeniedSnackbar()
        }
    }

    private fun observeSendStory(
        token: String,
        multipartBody: MultipartBody.Part,
        descRequestBody: RequestBody
    ) {
        var hasDialogAppeared = false
        viewModel.sendStory(
            token,
            multipartBody,
            descRequestBody
        )
            .observe(this@NewStoryActivity) {
                if (it is Result.Success) {
                    val intent = Intent(this@NewStoryActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else if (it is Result.Error) {
                    hideLoadingScreen()
                    if (!hasDialogAppeared) {
                        MaterialAlertDialogBuilder(this@NewStoryActivity)
                            .setTitle(getString(R.string.error))
                            .setMessage(it.error)
                            .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    hasDialogAppeared = true
                }
            }
    }

    private fun observeSendStoryWithLocation(
        token: String,
        multipartBody: MultipartBody.Part,
        descRequestBody: RequestBody,
        latRequestBody: RequestBody,
        lonRequestBody: RequestBody
    ) {
        var hasDialogAppeared = false
        viewModel.sendStoryWithLocation(
            token,
            multipartBody,
            descRequestBody,
            latRequestBody,
            lonRequestBody
        )
            .observe(this@NewStoryActivity) {
                if (it is Result.Success) {
                    val intent = Intent(this@NewStoryActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else if (it is Result.Error) {
                    hideLoadingScreen()
                    if (!hasDialogAppeared) {
                        MaterialAlertDialogBuilder(this@NewStoryActivity)
                            .setTitle(getString(R.string.error))
                            .setMessage(it.error)
                            .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    hasDialogAppeared = true
                }
            }
    }

    private fun setupView() {
        with(binding) {
            Glide.with(this@NewStoryActivity)
                .load(imageUri)
                .into(photo)
            name.text = loginResult?.name ?: getString(R.string.user)
        }
    }

    private fun setLocationSwitchListener() {
        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showLoadingScreen() {
        binding.loadingScreen.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.loadingScreen.visibility = View.GONE
    }

    private fun showPermissionDeniedSnackbar() {
        Snackbar.make(this, binding.root,
            getString(R.string.permission_denied), Snackbar.LENGTH_SHORT)
            .show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}