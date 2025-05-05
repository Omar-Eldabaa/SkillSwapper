package com.example.skillswapper.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.skillswapper.databinding.FragmentProfileBinding
import com.example.skillswapper.userskills.UserSkillsSetupActivity
import com.google.android.material.chip.Chip
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileFragmentViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let {
                binding.profileImage.setImageURI(it)
                uploadImageToFirebase(it)
            }
        } else {
            Toast.makeText(requireContext(), "No images selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference
            .child("profileImages/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Firebase.firestore.collection("users").document(userId)
                        .update("profileImageUrl", downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to Upload image", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileFragmentViewModel::class.java]
        val userId = Firebase.auth.currentUser?.uid
        viewModel.loadFullProfile(userId!!)
        observeData()
        binding.btnEditProfile.setOnClickListener {
            val intent =Intent(requireContext(),UserSkillsSetupActivity::class.java)
            startActivity(intent)
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }


    }

    private fun observeData() {
        viewModel.userData.observe(viewLifecycleOwner) { skills ->
            if (skills != null) {
                binding.userName.text = skills.first?.userName
                binding.preferredLanguage.text = skills.second?.preferredLanguage
                binding.bio.text = skills.second?.bio

                // Load known skills
                binding.knownSkillsGroup.removeAllViews()
                skills.second?.knownSkills?.forEach { skill ->
                    val chip = Chip(requireContext()).apply {
                        text = skill
                        isClickable = false
                        isCheckable = false
                    }
                    binding.knownSkillsGroup.addView(chip)
                }

                // Load desired skills
                binding.desiredSkillsGroup.removeAllViews()
                skills.second?.desiredSkills?.forEach { skill ->
                    val chip = Chip(requireContext()).apply {
                        text = skill
                        isClickable = false
                        isCheckable = false
                    }
                    binding.desiredSkillsGroup.addView(chip)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

}