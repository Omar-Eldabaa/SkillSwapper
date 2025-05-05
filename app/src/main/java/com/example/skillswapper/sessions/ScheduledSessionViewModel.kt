package com.example.skillswapper.sessions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillswapper.firestore.SessionsDao
import com.example.skillswapper.model.SessionWithDetails

class ScheduledSessionsViewModel : ViewModel() {

    private val _sessions = MutableLiveData<List<SessionWithDetails>>()
    val sessions: LiveData<List<SessionWithDetails>> get() = _sessions


    fun loadScheduledSessions(userId: String) {
        SessionsDao.getScheduledSessionsWithDetailsForUser(
            userId = userId,
            onSuccess = { sessionDetailsList ->
                _sessions.postValue(sessionDetailsList)
            },
            onFailure = {
            }
        )
    }
}
