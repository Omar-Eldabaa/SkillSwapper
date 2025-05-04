import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillswapper.firestore.ChatDao
import com.example.skillswapper.model.Chat

class ChatViewModel : ViewModel() {


    private val chatsLiveData = MutableLiveData<List<Chat>>()

    fun getChats(): LiveData<List<Chat>> = chatsLiveData

    fun loadChats(userId: String) {
        ChatDao.getUserChats(userId).addOnSuccessListener { snapshot ->
            val chats = snapshot.documents.mapNotNull {
                it.toObject(Chat::class.java)
            }
            chatsLiveData.postValue(chats)
        }
    }

}
