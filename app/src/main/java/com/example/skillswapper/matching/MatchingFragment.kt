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
import com.example.skillswapper.login.LoginActivity
import com.example.skillswapper.profileActivity.ProfileActivity
import com.example.skillswapper.recommendationSystem.MatchingUser
import com.example.skillswapper.showLoadingProgressDialog
import com.example.skillswapper.showMessage

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

        binding.logoutBtn.setOnClickListener{
                viewModel.logOut()
        }

         var loadingDialog:android.app.AlertDialog?=null

        viewModel.loadingLiveData.observe(viewLifecycleOwner){
            if (it==null){
                //Hide
                loadingDialog?.dismiss()
                loadingDialog =null
            }else{
                //Show
                loadingDialog=showLoadingProgressDialog(
                    message = it.message?:"",
                    isCancelable =it.isCancelable

                )
                loadingDialog?.show()
            }

        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) { message ->
            showMessage(
                message.message ?: "Something went wrong",
                posActionName = "Ok",
                posAction = message.posActionClick,
                negActionName = message.negActionName,
                neAction = message.negActionClick,
                isCancelable = message.isCancelable
            )
        }
        viewModel.event.observe(viewLifecycleOwner){
            when(it){
                MatchingViewEvent.NavigateToLogin->{
                    navigateToLogin()
                }

            }
            }


    }

    private fun navigateToLogin() {
        val intent =Intent(requireContext(),LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}