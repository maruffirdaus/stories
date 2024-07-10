package dev.maruffirdaus.stories.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import dev.maruffirdaus.stories.R
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.databinding.ActivityStoryBinding
import dev.maruffirdaus.stories.helper.DateHelper

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setToolbarMenuItemClick()
        setupView()
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    private fun setToolbarMenuItemClick() {
        binding.toolbar.setNavigationOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }

    private fun setupView() {
        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, StoryEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }

        if (story != null) {
            with(binding) {
                tvDetailName.text = story.name
                Glide.with(this@StoryActivity)
                    .load(story.photoUrl)
                    .into(ivDetailPhoto)
                date.text = DateHelper.covertDate(story.createdAt)
                tvDetailDescription.text = story.description
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}