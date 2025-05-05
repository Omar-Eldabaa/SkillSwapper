package com.example.skillswapper.profile


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillswapper.firestore.UserSkillsDao
import com.example.skillswapper.firestore.UsersDao
import com.example.skillswapper.model.User
import com.example.skillswapper.model.UserSkillsSetup
import com.google.android.gms.tasks.Tasks


class ProfileFragmentViewModel : ViewModel() {

    private val _userData = MutableLiveData<Pair<User?, UserSkillsSetup?>>()
    val userData: LiveData<Pair<User?, UserSkillsSetup?>> = _userData

    fun loadFullProfile(userId: String) {
        val userTask = UsersDao.getUserTask(userId)
        val skillsTask = UserSkillsDao.getUserSkillsTask(userId)

        Tasks.whenAllSuccess<Any>(userTask, skillsTask)
            .addOnSuccessListener { results ->
                val user = results[0] as? User
                val skills = results[1] as? UserSkillsSetup
                _userData.postValue(Pair(user, skills))
            }
            .addOnFailureListener {
                _userData.postValue(Pair(null, null))
            }
    }




}