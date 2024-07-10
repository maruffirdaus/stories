package dev.maruffirdaus.stories.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.LoginPreferences
import dev.maruffirdaus.stories.data.Result
import dev.maruffirdaus.stories.data.dataStore
import dev.maruffirdaus.stories.databinding.FragmentHomeBinding
import dev.maruffirdaus.stories.ui.MainViewModel
import dev.maruffirdaus.stories.ui.StoryAdapter
import dev.maruffirdaus.stories.ui.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private var loginResult: Set<String>? = null
    private val adapter = StoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        obtainViewModel()
        setToolbarMenuItemClick()
        setupScrollListener()
        setupRecyclerView()
        setNewStoryButton()
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                val intent = Intent(requireActivity(), NewStoryActivity::class.java)
                intent.putExtra(NewStoryActivity.EXTRA_IMAGE_URI, it.data!!.data.toString())
                requireActivity().startActivity(intent)
            }
        }

    private fun obtainViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity().application, requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private fun setToolbarMenuItemClick() {
        with(binding) {
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.refresh -> {
                        nestedScrollView.smoothScrollTo(nestedScrollView.scrollX, 0)
                        viewModel.getStories("Bearer " + (loginResult?.elementAt(2) ?: "token"))
                        true
                    }

                    R.id.action_logout -> {
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
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupScrollListener() {
        with(binding) {
            nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY == 0) {
                    newStoryButton.extend()
                } else {
                    newStoryButton.shrink()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.adapter = adapter

            val loginPref = LoginPreferences.getInstance(requireActivity().dataStore)
            loginResult = runBlocking { loginPref.getLoginResult().first() }
            viewModel.getStories("Bearer " + (loginResult?.elementAt(2) ?: "token"))

            viewModel.listStory.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> {
                        showLoadingBar()
                    }

                    is Result.Success -> {
                        lifecycleScope.launch {
                            adapter.setListStory(it.data)
                            delay(1000)
                            hideLoadingBar()
                            if (adapter.itemCount == 0) {
                                MaterialAlertDialogBuilder(requireActivity())
                                    .setMessage(getString(R.string.no_data))
                                    .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }
                    }

                    is Result.Error -> {
                        lifecycleScope.launch {
                            delay(1000)
                            hideLoadingBar()
                            MaterialAlertDialogBuilder(requireActivity())
                                .setTitle(getString(R.string.error))
                                .setMessage(it.error + ".")
                                .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun setNewStoryButton() {
        binding.newStoryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            launcherGallery.launch(intent)
        }
    }

    private fun showLoadingBar() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.loadingBar.visibility = View.GONE
    }
}