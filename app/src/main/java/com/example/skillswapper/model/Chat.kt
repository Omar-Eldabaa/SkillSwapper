package com.example.skillswapper.model

data class Chat(
    val id: String = "",                    // chatId
    val users: List<String> = listOf(),     // user1 & user2 IDs
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)
