package dev.maruffirdaus.stories.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.databinding.FragmentLoginBinding
import dev.maruffirdaus.stories.ui.MainViewModel
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.main.MainActivity

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadingScreen()
        obtainViewModel()
        setLoginButton()
        setRegisterButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLoadingScreen() {
        binding.loadingScreen.setOnTouchListener { _, _ -> true }
    }

    private fun obtainViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application, requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private fun setLoginButton() {
        with(binding) {
            loginButton.setOnClickListener {
                val email = edLoginEmail.editText?.text.toString()
                val password = edLoginPassword.editText?.text.toString()
                val areColumnsFilled = email.isNotEmpty() && password.isNotEmpty()
                edLoginEmail.error = null
                edLoginPassword.error = null

                if (areColumnsFilled) {
                    view?.clearFocus()
                    var hasDialogAppeared = false
                    viewModel.login(email, password).observe(viewLifecycleOwner) {
                        if (!hasDialogAppeared) {
                            when (it) {
                                is Result.Loading -> showLoadingScreen()
                                is Result.Success -> {
                                    viewModel.saveLoginResult(it.data.loginResult)
                                    requireActivity().startActivity(
                                        Intent(
                                            requireActivity(),
                                            MainActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()
                                }

                                is Result.Error -> {
                                    MaterialAlertDialogBuilder(requireActivity())
                                        .setTitle(getString(R.string.login_failed))
                                        .setMessage(it.error + ".")
                                        .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .show()
                                    hasDialogAppeared = true
                                    hideLoadingScreen()
                                }
                            }
                        }
                    }
                } else {
                    if (email.isNotEmpty()) {
                        edLoginPassword.error = getString(R.string.password_can_not_be_empty)
                    } else if (password.isNotEmpty()) {
                        edLoginEmail.error = getString(R.string.email_can_not_be_empty)
                    } else {
                        edLoginEmail.error = getString(R.string.email_can_not_be_empty)
                        edLoginPassword.error = getString(R.string.password_can_not_be_empty)
                    }
                }
            }
        }
    }

    private fun setRegisterButton() {
        binding.registerButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, RegisterFragment())
                addToBackStack("LoginFragment")
                commit()
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