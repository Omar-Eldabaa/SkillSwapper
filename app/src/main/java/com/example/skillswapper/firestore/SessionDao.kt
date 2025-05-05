package com.example.skillswapper.firestore

import com.example.skillswapper.model.Session
import com.example.skillswapper.model.SessionWithDetails
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object SessionsDao {
    private val db = FirebaseFirestore.getInstance()
    private val sessionsCollection = db.collection("sessions")

    fun createSession(session: Session, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val sessionId = UUID.randomUUID().toString()
        val newSession = session.copy(id = sessionId)
        sessionsCollection.document(sessionId)
            .set(newSession)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getIncomingSessionsForUser(userId: String, onSuccess: (List<Session>) -> Unit, onFailure: (Exception) -> Unit) {
        sessionsCollection
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "pending") // pending معناها لسه محتاجة قبول أو رفض
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.toObjects(Session::class.java)
                onSuccess(sessions)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun acceptSession(
        sessionId: String,
        senderId: String,
        receiverId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val participants = listOf(senderId, receiverId)

        sessionsCollection.document(sessionId)
            .update(
                mapOf(
                    "status" to "scheduled",
                    "participants" to participants
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    fun rejectSession(sessionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        sessionsCollection.document(sessionId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }



    fun getScheduledSessionsWithDetailsForUser(
        userId: String,
        onSuccess: (List<SessionWithDetails>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        sessionsCollection
            .whereArrayContains("participants", userId)
            .whereEqualTo("status", "scheduled")
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.toObjects(Session::class.java)
                if (sessions.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val db = FirebaseFirestore.getInstance()
                val usersCollection = db.collection("users")
                val skillsSetupCollectionName = "skillsSetup"

                val sessionDetailsList = mutableListOf<SessionWithDetails>()
                var loadedCount = 0

                sessions.forEach { session ->
                    // نحدد الطرف الآخر في الجلسة
                    val otherUserId = if (session.senderId == userId) session.receiverId else session.senderId

                    // نجيب اسم الطرف الآخر
                    usersCollection.document(otherUserId).get()
                        .addOnSuccessListener { userSnap ->
                            val otherUserName = userSnap.getString("userName") ?: "Unknown"

                            // نجيب المهارة من الـ sender (المفترض هو اللي بيعلم المهارة)
                            usersCollection.document(session.senderId)
                                .collection(skillsSetupCollectionName)
                                .document("skillsData")
                                .get()
                                .addOnSuccessListener { skillsSnap ->
                                    val knownSkills = skillsSnap.get("knownSkills") as? List<*> ?: emptyList<String>()
                                    val skillName = knownSkills.firstOrNull()?.toString() ?: "Skill"

                                    sessionDetailsList.add(
                                        SessionWithDetails(session, otherUserName, skillName)
                                    )

                                    loadedCount++
                                    if (loadedCount == sessions.size) {
                                        onSuccess(sessionDetailsList)
                                    }
                                }
                                .addOnFailureListener { onFailure(it) }
                        }
                        .addOnFailureListener { onFailure(it) }
                }
            }
            .addOnFailureListener { onFailure(it) }
    }



}
