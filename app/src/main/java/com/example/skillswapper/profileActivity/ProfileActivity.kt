package com.example.skillswapper.profileActivity

import ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.databinding.ActivityProfileBinding
import com.google.android.material.chip.Chip

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val userId = intent.getStringExtra("userId")
        if (userId != null) {
            viewModel.loadFullProfile(userId)
        }

        observeData()
    }

    private fun observeData() {
        viewModel.userData.observe(this) { skills ->
            if (skills != null) {
                binding.userName.text = skills.first?.userName
                binding.preferredLanguage.text = skills.second?.preferredLanguage
                binding.bio.text = skills.second?.bio

                // Load known skills
                binding.knownSkillsGroup.removeAllViews()
                skills.second?.knownSkills?.forEach { skill ->
                    val chip = Chip(this).apply {
                        text = skill
                        isClickable = false
                        isCheckable = false
                    }
                    binding.knownSkillsGroup.addView(chip)
                }

                // Load desired skills
                binding.desiredSkillsGroup.removeAllViews()
                skills.second?.desiredSkills?.forEach { skill ->
                    val chip = Chip(this).apply {
                        text = skill
                        isClickable = false
                        isCheckable = false
                    }
                    binding.desiredSkillsGroup.addView(chip)
                }
            } else {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
