package dev.maruffirdaus.stories.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.databinding.FragmentRegisterBinding
import dev.maruffirdaus.stories.ui.MainViewModel
import dev.maruffirdaus.stories.ui.ViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadingScreen()
        obtainViewModel()
        setRegisterButton()
        setCancelButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLoadingScreen() {
        binding.loadingScreen.setOnTouchListener { _, _ -> true }
    }

    private fun obtainViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application, requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private fun setRegisterButton() {
        with(binding) {
            registerButton.setOnClickListener {
                val name = edRegisterName.editText?.text.toString()
                val email = edRegisterEmail.editText?.text.toString()
                val password = edRegisterPassword.editText?.text.toString()
                val areAllColumnsFilled =
                    name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()

                if (areAllColumnsFilled && edRegisterEmail.error == null && edRegisterPassword.error == null) {
                    view?.clearFocus()
                    var hasDialogAppeared = false
                    viewModel.register(name, email, password).observe(viewLifecycleOwner) {
                        if (!hasDialogAppeared) {
                            when (it) {
                                is Result.Loading -> showLoadingScreen()
                                is Result.Success -> {
                                    showSimpleDialog(
                                        getString(R.string.registration_success),
                                        getString(R.string.registration_has_been_successful)
                                    )
                                    parentFragmentManager.popBackStack()
                                }

                                is Result.Error -> {
                                    showSimpleDialog(
                                        getString(R.string.registration_failed),
                                        it.error + "."
                                    )
                                    hasDialogAppeared = true
                                    hideLoadingScreen()
                                }
                            }
                        }
                    }
                } else {
                    showSimpleDialog(
                        getString(R.string.registration_failed),
                        getString(R.string.all_columns_must_be_filled_in_correctly)
                    )
                    hideLoadingScreen()
                }
            }
        }
    }

    private fun setCancelButton() {
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showLoadingScreen() {
        binding.loadingScreen.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.loadingScreen.visibility = View.GONE
    }

    private fun showSimpleDialog(title: String?, message: String?) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}