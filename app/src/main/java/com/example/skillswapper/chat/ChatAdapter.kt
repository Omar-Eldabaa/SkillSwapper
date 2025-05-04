package com.example.skillswapper.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.databinding.ChatItemBinding
import com.example.skillswapper.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val chatList: List<Chat>,
    private val itemClickListener: OnChatItemClickListener
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    interface OnChatItemClickListener {
        fun onChatItemClick(chat: Chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val otherUserId = chat.users.firstOrNull { it != currentUserId }

            // Get the other user's name from Firestore
            if (otherUserId != null) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(otherUserId)
                    .get()
                    .addOnSuccessListener { document: DocumentSnapshot ->
                        val userName = document.getString("userName") ?: "Unknown"
                        binding.userNameText.text = userName
                    }
            }
            binding.apply {
                lastMessageText.text = chat.lastMessage
                messageTimeText.text = formatTime(chat.lastTimestamp)

                root.setOnClickListener {
                    itemClickListener.onChatItemClick(chat)
                }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
