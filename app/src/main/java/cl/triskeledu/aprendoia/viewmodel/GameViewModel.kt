package cl.triskeledu.aprendoia.viewmodel
import androidx.lifecycle.*
import cl.triskeledu.aprendoia.data.repository.*
import kotlinx.coroutines.launch
class GameViewModel(private val repository:GameRepository):ViewModel() {
    val points=repository.points().asLiveData()
    fun score(userId:Long)=repository.score(userId).asLiveData()
    fun scan(userId:Long,payload:String,onResult:(ScanResult)->Unit)=viewModelScope.launch { onResult(repository.scanQr(userId,payload)) }
}
