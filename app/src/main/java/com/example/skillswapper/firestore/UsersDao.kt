package com.example.skillswapper.firestore

import com.example.skillswapper.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

object UsersDao {

    fun getUserCollection():CollectionReference{
        val database =Firebase.firestore
        return database.collection("users")

    }

    fun createUser(user: User, onCompleteListener:OnCompleteListener<Void>){
        val docRef = getUserCollection().document(user.id?:"")
        docRef.set(user)
            .addOnCompleteListener(onCompleteListener)
    }
    fun getUser(uid:String?,onCompleteListener:OnCompleteListener<DocumentSnapshot>){
        getUserCollection().document(uid?:"")
            .get().addOnCompleteListener(onCompleteListener)
    }
}