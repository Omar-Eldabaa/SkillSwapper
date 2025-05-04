package com.example.skillswapper.chat

import ChatViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.SessionProvider
import com.example.skillswapper.chatdetails.ChatDetailsActivity
import com.example.skillswapper.databinding.FragmentChatBinding
import com.example.skillswapper.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: MutableList<Chat> // قائمة الدردشات
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        val recyclerView: RecyclerView = binding.chatRecyclerView

        chatList = mutableListOf()
        chatAdapter = ChatAdapter(chatList, object : ChatAdapter.OnChatItemClickListener {
            override fun onChatItemClick(chat: Chat) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                val otherUserId = chat.users.firstOrNull { it != currentUserId } ?: return

                // Get the other user's name from Firestore
                FirebaseFirestore.getInstance().collection("users")
                    .document(otherUserId)
                    .get()
                    .addOnSuccessListener { document ->
                        val otherUserName = document.getString("userName") ?: "Unknown"

                        // Start ChatDetailsActivity and pass the necessary data
                        val intent = Intent(requireContext(), ChatDetailsActivity::class.java).apply {
                            putExtra("chatId", chat.id)
                            putExtra("otherUserId", otherUserId)
                            putExtra("otherUserName", otherUserName)
                        }
                        startActivity(intent)
                    }
            }

        })

        recyclerView.adapter = chatAdapter

        // نجيب الداتا من ViewModel باستخدام loadChats
        val userId = FirebaseAuth.getInstance().currentUser?.uid // هنا تضع ال userId الخاص باليوزر الذي تريد جلب الدردشات له
        viewModel.loadChats(userId?:"")

        // مراقبة التغييرات على الـ LiveData في الـ ViewModel
        viewModel.getChats().observe(viewLifecycleOwner) { chats ->
            chatList.clear()
            chatList.addAll(chats)
            chatAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
