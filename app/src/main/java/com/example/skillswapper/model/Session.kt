package com.example.skillswapper.model

data class Session(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val scheduledTime: Long = 0L,
    val status: String = "pending",
    val participants: List<String> = emptyList()

)
