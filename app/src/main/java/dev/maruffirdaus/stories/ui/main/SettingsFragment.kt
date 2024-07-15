package dev.maruffirdaus.stories.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.source.local.preferences.dataStore
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import dev.maruffirdaus.stories.databinding.FragmentSettingsBinding
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var loginResult: LoginResult? = null
    private lateinit var viewModel: MainViewModel

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
        obtainViewModel()
        setupView()
        setLogoutButton()
        setupLanguageSettings()
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
        val factory = ViewModelFactory.getInstance(requireActivity().application, requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private fun setupView() {
        binding.name.text = loginResult?.name ?: getString(R.string.user)
    }

    private fun setLogoutButton() {
        binding.actionLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.logout) + "?")
                .setMessage(getString(R.string.you_will_be_logged_out))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.clearLoginResult()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupLanguageSettings() {
        binding.languageSettings.setOnClickListener {
            requireActivity().startActivity(Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS))
        }
    }
}