package com.example.skillswapper.userskills

import UserSkillsSetupViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.R
import com.example.skillswapper.databinding.ActivityUserSkillsSetupBinding
import com.example.skillswapper.home.HomeActivity
import com.example.skillswapper.model.UserSkillsSetup
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class UserSkillsSetupActivity : AppCompatActivity() {
    private lateinit var viewModel: UserSkillsSetupViewModel
    private lateinit var viewBinding: ActivityUserSkillsSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityUserSkillsSetupBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
        initViews()
        subscribeToLiveData()
//        viewBinding.btnSave.setOnClickListener {
//            val knownSkills = getSelectedSkills(viewBinding.skillsContainer)
//            val desiredSkills = getSelectedSkills(viewBinding.skillsContainerDesired)
//
//            if (viewModel.isValidSelection(knownSkills, desiredSkills)) {
//                // Proceed to save in Firestore or next step
//                Toast.makeText(this, "Valid data! Proceeding...", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Please select at least one known and one desired skill.", Toast.LENGTH_SHORT).show()
//            }
//        }

        viewBinding.btnSave.setOnClickListener {
            val knownSkills = getSelectedSkills(viewBinding.skillsContainer)
            val desiredSkills = getSelectedSkills(viewBinding.skillsContainerDesired)
            val knownCategory = viewBinding.spinnerCategory.selectedItem?.toString()
            val desiredCategory = viewBinding.spinnerCategoryDesired.selectedItem?.toString()
            val preferredLanguage = viewBinding.spinnerLanguages.selectedItem?.toString()
            val bio = viewBinding.etAboutYou.text.toString()
            val userId = Firebase.auth.currentUser?.uid

            if (viewModel.isValidSelection(knownSkills, desiredSkills)) {
                val skillsSetup = UserSkillsSetup(
                    userId = userId,
                    knownSkills = knownSkills,
                    knownCategory = knownCategory,
                    desiredSkills = desiredSkills,
                    desiredCategory = desiredCategory,
                    preferredLanguage = preferredLanguage,
                    bio = bio
                )

                viewModel.saveUserSkills(skillsSetup,
                    onSuccess = {
                        Toast.makeText(this, "Skills saved successfully!", Toast.LENGTH_SHORT).show()
                        // navigate to next screen here if needed
                        navigateToHome()
                    },
                    onFailure = {
                        Toast.makeText(this, "Failed to save skills: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Toast.makeText(this, "Please select at least one known and one desired skill.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun navigateToHome() {
        val intent =Intent(this ,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[UserSkillsSetupViewModel::class.java]

        viewModel.loadCategoriesFromAssets()
    }

    private fun subscribeToLiveData() {
        // Shared categories observer
        viewModel.categories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }

            // Spinner for owned skills
            val ownedAdapter = ArrayAdapter(
                this,
                R.layout.custom_spinner_item,
                categoryNames
            )
            ownedAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            viewBinding.spinnerCategory.adapter = ownedAdapter
            viewBinding.spinnerCategory.background =
                ContextCompat.getDrawable(this, R.drawable.custom_spinner_background)

            viewBinding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categoryNames[position]
                    viewModel.onOwnedCategorySelected(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            // Spinner for desired skills
            val desiredAdapter = ArrayAdapter(
                this,
                R.layout.custom_spinner_item,
                categoryNames
            )
            desiredAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            viewBinding.spinnerCategoryDesired.adapter = desiredAdapter
            viewBinding.spinnerCategoryDesired.background =
                ContextCompat.getDrawable(this, R.drawable.custom_spinner_background)

            viewBinding.spinnerCategoryDesired.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categoryNames[position]
                    viewModel.onDesiredCategorySelected(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Show owned skills checkboxes
        viewModel.ownedSkills.observe(this) { skills ->
            viewBinding.skillsContainer.removeAllViews()
            skills.forEach { skill ->
                val checkBox = CheckBox(this).apply {
                    text = skill
                    setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
                    setPadding(8, 8, 8, 8)
                }
                viewBinding.skillsContainer.addView(checkBox)
            }
        }

        // Show desired skills checkboxes
        viewModel.desiredSkills.observe(this) { skills ->
            viewBinding.skillsContainerDesired.removeAllViews()
            skills.forEach { skill ->
                val checkBox = CheckBox(this).apply {
                    text = skill
                    setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
                    setPadding(8, 8, 8, 8)
                }
                viewBinding.skillsContainerDesired.addView(checkBox)
            }
        }

        // Languages spinner
        viewModel.languages.observe(this) { languages ->
            val adapter = ArrayAdapter(
                this,
                R.layout.custom_spinner_item,
                languages.map { it.name }
            )
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            viewBinding.spinnerLanguages.adapter = adapter
            viewBinding.spinnerLanguages.background =
                ContextCompat.getDrawable(this, R.drawable.custom_spinner_background)
        }


    }
    fun getSelectedSkills(container: ViewGroup): List<String> {
        val selectedSkills = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                selectedSkills.add(view.text.toString())
            }
        }
        return selectedSkills
    }
}




