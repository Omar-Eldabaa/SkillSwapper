package com.example.skillswapper.chatdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillswapper.R
import com.example.skillswapper.databinding.ActivityChatDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Date

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
        viewBinding.sessionRequestButton.setOnClickListener {
            showScheduleDialog()
        }
    }

    private fun showScheduleDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_session, null)

        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        confirmButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            val hour = timePicker.hour
            val minute = timePicker.minute

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }


            val timestamp = calendar.timeInMillis

            sendSessionRequestToOtherUser(timestamp)

            dialog.dismiss()
        }
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun sendSessionRequestToOtherUser(timestamp: Long) {
        viewModel.createSession(
            receiverId = otherUserId,
            scheduledTime = timestamp,
            onSuccess = {
                Toast.makeText(this, "Session request sent successfully", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }



}


