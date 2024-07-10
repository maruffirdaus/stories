package dev.maruffirdaus.stories.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.dataStore
import dev.maruffirdaus.stories.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var loginResult: Set<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLoginResult()
        setupView()
        setupLanguageSettings()
    }

    private fun getLoginResult() {
        val loginPref = LoginPreferences.getInstance(requireActivity().dataStore)
        loginResult = runBlocking { loginPref.getLoginResult().first() }
    }

    private fun setupView() {
        binding.name.text = loginResult?.elementAt(1) ?: getString(R.string.user)
    }

    private fun setupLanguageSettings() {
        binding.languageSettings.setOnClickListener {
            requireActivity().startActivity(Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS))
        }
    }
}