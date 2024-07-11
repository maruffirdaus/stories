package dev.maruffirdaus.stories.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import dev.maruffirdaus.stories.databinding.ActivityMainBinding
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.auth.AuthActivity
import dev.maruffirdaus.stories.ui.main.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var loginResult: LoginResult? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoadingScreen()
        setInsets()
        setupNavigation()
        obtainViewModel()
        initCheck()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLoadingScreen() {
        binding.loadingScreen.setOnTouchListener { _, _ -> true }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.loadingScreen) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun obtainViewModel() {
        val factory = ViewModelFactory.getInstance(application, this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun initCheck() {
        viewModel.getLoginResult().observe(this) {
            if (it == null) {
                showLoadingScreen()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            } else {
                loginResult = LoginResult(it.elementAt(0), it.elementAt(1), it.elementAt(2))
                hideLoadingScreen()
            }
        }
    }

    private fun showLoadingScreen() {
        binding.loadingScreen.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.loadingScreen.visibility = View.GONE
    }
}