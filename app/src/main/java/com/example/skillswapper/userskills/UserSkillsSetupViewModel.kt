import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.common.SingleLiveEvent
import com.example.skillswapper.firestore.UserSkillsDao
import com.example.skillswapper.model.Language
import com.example.skillswapper.model.SkillCategory
import com.example.skillswapper.model.UserSkillsSetup
import com.example.skillswapper.userskills.UserSetupViewEvent
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class UserSkillsSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val _categories = MutableLiveData<List<SkillCategory>>()
    val categories: LiveData<List<SkillCategory>> get() = _categories

    private val _ownedSkills = MutableLiveData<List<String>>()
    val ownedSkills: LiveData<List<String>> get() = _ownedSkills

    private val _desiredSkills = MutableLiveData<List<String>>()
    val desiredSkills: LiveData<List<String>> get() = _desiredSkills

    private val _languages = MutableLiveData<List<Language>>()
    val languages: LiveData<List<Language>> get() = _languages

    val events =SingleLiveEvent<UserSetupViewEvent>()

    fun loadCategoriesFromAssets() {
        val context = getApplication<Application>()
        val jsonString = context.assets.open("skills_data.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<SkillCategory>>() {}.type
        val categoryList: List<SkillCategory> = Gson().fromJson(jsonString, type)
        _categories.postValue(categoryList)
    }

    fun onOwnedCategorySelected(categoryName: String) {
        val category = _categories.value?.find { it.name == categoryName }
        _ownedSkills.value = category?.skills ?: emptyList()
    }

    fun onDesiredCategorySelected(categoryName: String) {
        val category = _categories.value?.find { it.name == categoryName }
        _desiredSkills.value = category?.skills ?: emptyList()
    }

    fun isValidSelection(knownSkills: List<String>, desiredSkills: List<String>): Boolean {
        return knownSkills.isNotEmpty() && desiredSkills.isNotEmpty()
    }

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _languages.value = listOf(
            Language("en", "English"),
            Language("ar", "Arabic"),
            Language("es", "Español"),
            Language("fr", "Français"),
            Language("de", "Deutsch")
        )
    }



    fun saveUserSkills(
        userSkills: UserSkillsSetup,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        UserSkillsDao.saveUserSkills(userSkills.userId, userSkills) { task ->
            if (task.isSuccessful) {
                onSuccess()
//                navigateToHome()
            } else {
                onFailure(task.exception ?: Exception("Unknown error occurred"))
            }
        }
    }




//    fun navigateToHome(){
//       events.postValue(UserSetupViewEvent.NavigateToHome)
//   }







}

