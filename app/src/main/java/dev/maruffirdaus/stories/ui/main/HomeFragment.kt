package dev.maruffirdaus.stories.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.local.preferences.LoginPreferences
import dev.maruffirdaus.stories.data.source.local.preferences.dataStore
import dev.maruffirdaus.stories.data.source.remote.response.LoginResult
import dev.maruffirdaus.stories.databinding.FragmentHomeBinding
import dev.maruffirdaus.stories.ui.ViewModelFactory
import dev.maruffirdaus.stories.ui.adapter.LoadingStateAdapter
import dev.maruffirdaus.stories.ui.adapter.StoryAdapter
import dev.maruffirdaus.stories.ui.main.viewmodel.MainViewModel
import dev.maruffirdaus.stories.ui.recyclerview.itemdecoration.DividerItemDecoration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var loginResult: LoginResult? = null
    private lateinit var viewModel: MainViewModel
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
        getLoginResult()
        obtainViewModel()
        setToolbarMenuItemClick()
        setupRecyclerView()
        setNewStoryButton()
        showScrollUpSnackbar()
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                val intent = Intent(requireActivity(), NewStoryActivity::class.java)
                intent.putExtra(NewStoryActivity.EXTRA_IMAGE_URI, it.data!!.data.toString())
                requireActivity().startActivity(intent)
            }
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

    private fun setToolbarMenuItemClick() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    adapter.refresh()
                    showScrollUpSnackbar()
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

    private fun setupRecyclerView() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.itemAnimator = null
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    R.drawable.divider
                )
            )
            recyclerView.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            recyclerView.scrollToPosition(0)

            viewModel.getStories("Bearer " + (loginResult?.token ?: "token"))
                .observe(viewLifecycleOwner) {
                    adapter.submitData(lifecycle, it)
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

    private fun showScrollUpSnackbar() {
        with(binding) {
            Snackbar.make(
                root,
                getString(R.string.scroll_up_to_see_recent_stories),
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView(newStoryButton)
                .setAction(getString(R.string.scroll_up)) {
                    recyclerView.smoothScrollToPosition(0)
                }
                .show()
        }
    }
}