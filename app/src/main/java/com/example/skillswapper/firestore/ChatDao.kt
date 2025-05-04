package com.example.skillswapper.firestore

import com.example.skillswapper.model.Chat
import com.example.skillswapper.model.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

object ChatDao {

    private val db = Firebase.firestore

    // Create or fetch existing chat between two users
    fun getOrCreateChatId(user1: String, user2: String): Task<String> {
        val chatsRef = db.collection("chats")
        val query = chatsRef.whereArrayContains("users", user1)

        return query.get().continueWith { task ->
            val snapshot = task.result
            val existingChat = snapshot?.documents?.find {
                val users = it["users"] as? List<*>
                users?.contains(user2) == true
            }

            if (existingChat != null) {
                existingChat.id
            } else {
                // Create new chat
                val chatDoc = chatsRef.document()
                val newChat = Chat(
                    id = chatDoc.id,
                    users = listOf(user1, user2)
                )
                chatDoc.set(newChat)
                chatDoc.id
            }
        }
    }

    fun sendMessage(chatId: String, message: Message): Task<Void> {
        val messageRef = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        val msgWithId = message.copy(id = messageRef.id)

        return messageRef.set(msgWithId).continueWithTask {
            // Update last message in chat
            db.collection("chats")
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to msgWithId.content,
                        "lastTimestamp" to msgWithId.timestamp
                    )
                )
        }
    }


    fun getMessages(chatId: String): Task<QuerySnapshot> {
        return db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
    }
    fun getUserChats(userId: String): Task<QuerySnapshot> {
        return db.collection("chats")
            .whereArrayContains("users", userId)
//            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .get()
    }



}
