package com.example.skillswapper.chatdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillswapper.databinding.ActivityChatDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class ChatDetailsActivity : AppCompatActivity() {
    lateinit var viewModel: ChatDetailsViewModel
    lateinit var viewBinding: ActivityChatDetailsBinding

    private lateinit var chatId: String
    private lateinit var otherUserId: String
    private lateinit var otherUserName: String

    private lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =ActivityChatDetailsBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
        initViews()
        chatId = intent.getStringExtra("chatId") ?: ""
        otherUserId = intent.getStringExtra("otherUserId") ?: ""
        otherUserName = intent.getStringExtra("otherUserName") ?: ""

        // Set up the toolbar
        setupToolbar()

        // Set up RecyclerView
        messageAdapter = MessageAdapter(emptyList(), FirebaseAuth.getInstance().currentUser?.uid ?: "",otherUserName)
        viewBinding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.messagesRecyclerView.adapter = messageAdapter

        // Observe messages
        viewModel.messages.observe(this) { messages ->
            messageAdapter = MessageAdapter(messages, FirebaseAuth.getInstance().currentUser?.uid ?: "",otherUserName)
            viewBinding.messagesRecyclerView.adapter = messageAdapter
            messageAdapter.notifyDataSetChanged()
        }

        // Load messages
        viewModel.loadMessages(chatId)

        // Send message on button click
        viewBinding.sendButton.setOnClickListener {
            val messageText = viewBinding.messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(chatId, messageText)
                viewBinding.messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        viewBinding.userName.text = otherUserName
        viewBinding.backButton.setOnClickListener { finish() }
    }
    private fun initViews() {
        viewModel =ViewModelProvider(this)[ChatDetailsViewModel::class.java]
    }
    }


