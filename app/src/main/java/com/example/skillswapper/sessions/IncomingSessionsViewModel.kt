package com.example.skillswapper.sessions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillswapper.firestore.SessionsDao
import com.example.skillswapper.firestore.UserSkillsDao
import com.example.skillswapper.firestore.UsersDao
import com.example.skillswapper.model.SessionWithDetails

class IncomingSessionsViewModel : ViewModel() {

    private val _incomingSessions = MutableLiveData<List<SessionWithDetails>>()
    val incomingSessions: LiveData<List<SessionWithDetails>> = _incomingSessions

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadIncomingSessions(currentUserId: String) {
        _loading.value = true

        SessionsDao.getIncomingSessionsForUser(
            userId = currentUserId,
            onSuccess = { sessions ->
                if (sessions.isEmpty()) {
                    _incomingSessions.value = emptyList()
                    _loading.value = false
                  return@getIncomingSessionsForUser
                }

                val results = mutableListOf<SessionWithDetails>()
                var loadedCount = 0

                sessions.forEach { session ->
                    UsersDao.getUserTask(session.senderId).addOnSuccessListener { user ->
                        UserSkillsDao.getUserSkillsTask(session.senderId).addOnSuccessListener { skillsSetup ->
                            val senderName = user?.userName ?: "Unknown"
                            val skillName = skillsSetup?.knownSkills?.firstOrNull() ?: "N/A"

                            results.add(SessionWithDetails(session, senderName, skillName))
                            loadedCount++

                            if (loadedCount == sessions.size) {
                                _incomingSessions.value = results
                                _loading.value = false
                            }
                        }.addOnFailureListener {
                            loadedCount++
                            if (loadedCount == sessions.size) {
                                _incomingSessions.value = results
                                _loading.value = false
                            }
                        }
                    }.addOnFailureListener {
                        loadedCount++
                        if (loadedCount == sessions.size) {
                            _incomingSessions.value = results
                            _loading.value = false
                        }
                    }
                }
            },
            onFailure = {
                _incomingSessions.value = emptyList()
                _loading.value = false
            }
        )
    }

    fun acceptSession(sessionWithDetails: SessionWithDetails) {
        val session = sessionWithDetails.session

        SessionsDao.acceptSession(
            sessionId = session.id,
            senderId = session.senderId,
            receiverId = session.receiverId,
            onSuccess = {
                _incomingSessions.value = _incomingSessions.value?.filterNot { it.session.id == session.id }
            },
            onFailure = {
                // Handle error if needed
            }
        )
    }


    fun rejectSession(sessionId: String) {
        SessionsDao.rejectSession(sessionId,
            onSuccess = {
                _incomingSessions.value = _incomingSessions.value?.filterNot { it.session.id == sessionId }
            },
            onFailure = {
            }
        )
    }


}
