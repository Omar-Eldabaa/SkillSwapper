package com.example.skillswapper.chatdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skillswapper.firestore.ChatDao
import com.example.skillswapper.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot

class ChatDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load messages between the users
    fun loadMessages(chatId: String) {
        ChatDao.getMessages(chatId).addOnSuccessListener { querySnapshot: QuerySnapshot ->
            val messageList = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Message::class.java)
            }
            _messages.value = messageList
        }
    }

    // Send a new message
    fun sendMessage(chatId: String, message: String) {
        val newMessage = Message(
            senderId = currentUserId,
            receiverId = "",  // Will get this from chat details
            content = message,
            timestamp = System.currentTimeMillis()
        )

        ChatDao.sendMessage(chatId, newMessage).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Optionally reload messages or update UI
                loadMessages(chatId)
            }
        }
    }
}
