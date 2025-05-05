package com.example.skillswapper.firestore

import com.example.skillswapper.model.UserSkillsSetup
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object UserSkillsDao {

    private fun getUserSkillsDocument(userId: String?): DocumentReference {
        val db = Firebase.firestore
        return db.collection("users")
            .document(userId ?: "")
            .collection("skillsSetup")
            .document("skillsData")
    }

    fun saveUserSkills(
        userId: String?,
        userSkills: UserSkillsSetup,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        getUserSkillsDocument(userId)
            .set(userSkills)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUserSkills(
        userId: String?,
        onCompleteListener: OnCompleteListener<UserSkillsSetup>
    ) {
        getUserSkillsDocument(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val skills = documentSnapshot.toObject(UserSkillsSetup::class.java)
                onCompleteListener.onComplete(
                    com.google.android.gms.tasks.Tasks.forResult(skills)
                )
            }
            .addOnFailureListener {
                onCompleteListener.onComplete(
                    com.google.android.gms.tasks.Tasks.forException(it)
                )
            }
    }

    fun getUserSkillsTask(userId: String): Task<UserSkillsSetup?> {
        return getUserSkillsDocument(userId)
            .get()
            .continueWith { task ->
                task.result?.toObject(UserSkillsSetup::class.java)
            }
    }
}
