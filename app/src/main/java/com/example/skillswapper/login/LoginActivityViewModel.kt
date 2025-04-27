package com.example.skillswapper.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.common.SingleLiveEvent
import com.example.skillswapper.Message
import com.example.skillswapper.SessionProvider
import com.example.skillswapper.firestore.UserSkillsDao
import com.example.skillswapper.firestore.UsersDao
import com.example.skillswapper.model.User
import com.example.skillswapper.model.UserSkillsSetup
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LoginActivityViewModel:ViewModel() {
    val emailLv = MutableLiveData<String>("omar@gmail.com")
    val passwordLv = MutableLiveData<String>("123456")
    val isLoading = MutableLiveData<Boolean>()
    val messageLiveData = SingleLiveEvent<Message>()
    val events =SingleLiveEvent<LoginViewEvent>()

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()


    val auth =Firebase.auth

    fun login() {
        if (!validate()) return
        isLoading.postValue(true)
        auth.signInWithEmailAndPassword(emailLv.value!!, passwordLv.value!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    isLoading.postValue(false)
                    getUserFromFirestore(it.result.user?.uid)

                } else {
                    isLoading.postValue(false)
                    //show Error
                    messageLiveData.postValue(
                        Message(message = it.exception?.localizedMessage)
                    )

                }
            }
    }

    private fun getUserFromFirestore(uid: String?) {
        isLoading.postValue(true)
        UsersDao.getUser(uid){
            isLoading.postValue(false)
            if (it.isSuccessful){
                SessionProvider.user=it.result.toObject(User::class.java)
                checkIfSkillsSetupExists(uid)

            }else{
                messageLiveData.postValue(
                    Message(message = it.exception?.localizedMessage)
                )

            }
        }

    }
    private fun checkIfSkillsSetupExists(userId: String?) {
        UserSkillsDao.getUserSkills(userId) {
            val userSkillsSetup = it.result
            if (it.isSuccessful && userSkillsSetup != null) {
                // المستخدم عنده بيانات إعداد المهارات
                events.postValue(LoginViewEvent.NavigateToHome)
            } else {
                // مفيش بيانات إعداد المهارات، نوديه يعملها
                events.postValue(LoginViewEvent.NavigateToUserSkillsSetup)
            }
        }
    }


    private fun validate(): Boolean {
            var isValid = true

            if (emailLv.value.isNullOrBlank()) {
                emailError.postValue("enter your Email")
                isValid = false

            } else {
                emailError.postValue(null)

            }
            if (passwordLv.value.isNullOrBlank()) {
                passwordError.postValue("enter your password")
                isValid = false

            } else {
                passwordError.postValue(null)

            }
            return isValid
        }

        fun navigateToRegister() {
            events.postValue(LoginViewEvent.NavigateToRegister)
        }

    }