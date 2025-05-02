package com.example.skillswapper.matching

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.databinding.FragmentMatchingBinding
import com.example.skillswapper.profileActivity.ProfileActivity
import com.example.skillswapper.recommendationSystem.MatchingUser

class MatchingFragment : Fragment() {

    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MatchingViewModel
    private lateinit var adapter: MatchingUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MatchingViewModel::class.java]
        adapter = MatchingUsersAdapter()
        binding.matchingRecyclerView.adapter = adapter

        // مراقبة الـ ViewModel لجلب قائمة المستخدمين المتوافقين
        viewModel.usersList.observe(viewLifecycleOwner, Observer { matchingUsers ->
            adapter.submitList(matchingUsers)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getUsers()



        adapter.listener = object : OnProfileClickListener {
            override fun onViewProfileClick(userId: String) {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }

            override fun onSendMessageClick(user: MatchingUser) {
                val bottomSheet = SendMessageBottomSheet(
                    receiverId = user.userSkills.userId?:"",
                    receiverName = user.userName
                ) { messageText ->
                    viewModel.sendMessageToUser(user.userSkills.userId?:"", messageText) // هنعملها في الخطوة الجاية
                }
                bottomSheet.show(parentFragmentManager, "SendMessageBottomSheet")
            }



        }





    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}