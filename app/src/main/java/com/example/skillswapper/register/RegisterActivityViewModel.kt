package com.example.skillswapper.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.common.SingleLiveEvent
import com.example.skillswapper.Message
import com.example.skillswapper.SessionProvider
import com.example.skillswapper.firestore.UsersDao
import com.example.skillswapper.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivityViewModel:ViewModel() {
    val userNameLv = MutableLiveData<String>()
    val emailLv = MutableLiveData<String>()
    val passwordLv = MutableLiveData<String>()
    val passwordConLv = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val messageLiveData = SingleLiveEvent<Message>()
    val events= SingleLiveEvent<RegisterViewEvent>()

    val userNameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val passwordConError = MutableLiveData<String?>()

    val auth =Firebase.auth

    fun register(){
        if (!validate())return
        isLoading.postValue(true)
        auth.createUserWithEmailAndPassword(emailLv.value!!,passwordLv.value!!)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    insertUserToFirestore(uid = it.result.user?.uid)


                }else{
                    isLoading.postValue(false)
                    //show Error
                    messageLiveData.postValue(
                        Message(message = it.exception?.localizedMessage)
                    )

                }
            }

    }
    private fun insertUserToFirestore(uid:String?) {
        val user = User(
            id = uid ,
            userName = userNameLv.value ,
            email = emailLv.value
        )

        UsersDao.createUser(user){ task->
            if (task.isSuccessful){
                isLoading.postValue(false)
                messageLiveData.postValue(Message(
                    message = "User Registered is Successfully",
                    posActionName = "ok",
                    posActionClick = {
                        // save user
                        SessionProvider.user=user
                        //navigate to Home screen
                        events.postValue(RegisterViewEvent.NavigateToSetupSkills)



                    }
                ))

            }else{
                messageLiveData.postValue(
                    Message(message = task.exception?.localizedMessage)
                )


            }

        }
    }

    private fun validate(): Boolean {
        var isValid =true
        if (userNameLv.value.isNullOrBlank()){
            userNameError.postValue("enter your user name")
            isValid=false

        }else{
            userNameError.postValue(null)

        }
        if (emailLv.value.isNullOrBlank()){
            emailError.postValue("enter your Email")
            isValid=false

        }else{
            emailError.postValue(null)

        }
        if (passwordLv.value.isNullOrBlank()){
            passwordError.postValue("enter your password")
            isValid=false

        }else{
            passwordError.postValue(null)

        }
        if (passwordConLv.value.isNullOrBlank() || passwordConLv.value!=passwordLv.value ){
            passwordConError.postValue("enter your password confirmation again")
            isValid=false

        }else{
            passwordConError.postValue(null)

        }
        return isValid


    }
    fun navigateToLogin(){
        events.postValue(RegisterViewEvent.NavigateToLogin)
    }
}