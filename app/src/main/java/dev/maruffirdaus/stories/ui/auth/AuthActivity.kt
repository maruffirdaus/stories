package dev.maruffirdaus.stories.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        loadFragment()
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            if (0 < ime.bottom) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, ime.bottom)
            } else {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            insets
        }
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, LoginFragment())
            commit()
        }
    }
}