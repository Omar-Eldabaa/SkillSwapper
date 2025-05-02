package com.example.skillswapper.matching

import androidx.lifecycle.*
import com.example.skillswapper.firestore.ChatDao
import com.example.skillswapper.firestore.UserSkillsDao
import com.example.skillswapper.firestore.UsersDao
import com.example.skillswapper.model.Message
import com.example.skillswapper.model.User
import com.example.skillswapper.recommendationSystem.MatchingUser
import com.example.skillswapper.recommendationSystem.RecommendationSystem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MatchingViewModel : ViewModel() {

    private val _usersList = MutableLiveData<List<MatchingUser>>()
    val usersList: LiveData<List<MatchingUser>> get() = _usersList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun getUsers() {
        if (userId == null) {
            _errorMessage.value = "User not authenticated"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    val snapshot = UsersDao.getUserCollection().get().await()
                    snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                }

                getRecommendations(users)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load users: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private suspend fun getRecommendations(users: List<User>) {
        if (userId == null) return

        try {
            val currentUserSkills = withContext(Dispatchers.IO) {
                UserSkillsDao.getUserSkillsTask(userId).await()
            }

            if (currentUserSkills == null) {
                _errorMessage.value = "No skills found for the current user."
                _isLoading.value = false
                return
            }

            val otherUsers = users.filter { it.id != userId && it.id != null }
            val allUserNames = users.associateBy { it.id }.mapValues { it.value.userName ?: "Unknown" }

            val otherUserSkills = withContext(Dispatchers.IO) {
                otherUsers.mapNotNull { user ->
                    val uid = user.id ?: return@mapNotNull null
                    val skills = UserSkillsDao.getUserSkillsTask(uid).await()
                    skills?.copy(userId = uid)
                }
            }

            val matchingUsers = RecommendationSystem.getMatchingUsers(
                currentUserSkills,
                otherUserSkills,
                allUserNames
            )

            _usersList.value = matchingUsers
        } catch (e: Exception) {
            _errorMessage.value = "Error getting recommendations: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }



    fun sendMessageToUser(receiverId: String, messageText: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            _errorMessage.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            try {
                val chatId = withContext(Dispatchers.IO) {
                    ChatDao.getOrCreateChatId(currentUserId, receiverId).await()
                }

                val message = Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    content = messageText,
                    timestamp = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    ChatDao.sendMessage(chatId, message).await()
                }

                _errorMessage.value = "Message sent successfully"

            } catch (e: Exception) {
                _errorMessage.value = "Failed to send message: ${e.message}"
            }
        }
    }

}
