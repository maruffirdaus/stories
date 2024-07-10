package dev.maruffirdaus.stories.ui.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.dataStore
import dev.maruffirdaus.stories.databinding.ActivityNewStoryBinding
import dev.maruffirdaus.stories.helper.reduceFileImage
import dev.maruffirdaus.stories.helper.uriToFile
import dev.maruffirdaus.stories.ui.MainViewModel
import dev.maruffirdaus.stories.ui.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private var loginResult: Set<String>? = null
    private var imageUri: String? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        getExtra()
        getLoginResult()
        obtainViewModel()
        setToolbarMenuItemClick()
        setupView()
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

    private fun getExtra() {
        imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
    }

    private fun getLoginResult() {
        val loginPref = LoginPreferences.getInstance(dataStore)
        loginResult = runBlocking { loginPref.getLoginResult().first() }
    }

    private fun obtainViewModel() {
        val factory = ViewModelFactory.getInstance(application, this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
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
                            sendStory(desc.toString())
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

    private fun sendStory(desc: String) {
        if (imageUri != null) {
            lifecycleScope.launch {
                val token = "Bearer " + (loginResult?.elementAt(2) ?: "token")
                val multipartBody: MultipartBody.Part
                val requestBody = desc.toRequestBody("text/plain".toMediaType())

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

                var hasDialogAppeared = false
                viewModel.sendStory(token, multipartBody, requestBody)
                    .observe(this@NewStoryActivity) {
                        if (it is Result.Success) {
                            viewModel.getStories(token)
                            finish()
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
        }
    }

    private fun setupView() {
        with(binding) {
            Glide.with(this@NewStoryActivity)
                .load(imageUri)
                .into(photo)
            name.text = loginResult?.elementAt(1) ?: getString(R.string.user)
            edAddDescription.editText?.requestFocus()
            lifecycleScope.launch {
                delay(200)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                @Suppress("DEPRECATION")
                imm.toggleSoftInputFromWindow(
                    edAddDescription.applicationWindowToken,
                    InputMethodManager.SHOW_IMPLICIT,
                    0
                )
            }
        }
    }

    private fun showLoadingScreen() {
        binding.loadingScreen.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.loadingScreen.visibility = View.GONE
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}