package com.example.skillswapper.model

data class Message(
    val id: String = "",                  // messageId (optional, for clarity)
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
