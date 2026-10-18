package cl.triskeledu.aprendoia.viewmodel
import androidx.lifecycle.*
import cl.triskeledu.aprendoia.data.repository.GameRepository
import cl.triskeledu.aprendoia.data.entities.UserEntity
import kotlinx.coroutines.launch
class AuthViewModel(private val repository:GameRepository):ViewModel() {
    val users=repository.users().asLiveData()
    private val _activeUser=MutableLiveData<UserEntity?>(null)
    val activeUser:LiveData<UserEntity?> = _activeUser
    fun login(alias:String,password:String,onResult:(Boolean)->Unit)=viewModelScope.launch {
        _activeUser.value=repository.login(alias,password)
        onResult(_activeUser.value!=null)
    }
    fun logout(){ _activeUser.value=null }
}
