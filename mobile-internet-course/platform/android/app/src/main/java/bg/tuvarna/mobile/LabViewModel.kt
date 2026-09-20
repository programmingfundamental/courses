package bg.tuvarna.mobile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
data class LabUiState(val message:String="Готов starter — реализирайте текущия TODO",val baseUrl:String="http://10.0.2.2:8080/api/")
class LabViewModel:ViewModel(){
 private val mutable=MutableStateFlow(LabUiState());val state:StateFlow<LabUiState> = mutable.asStateFlow();private var job:Job?=null
 fun baseUrl(value:String){mutable.update{it.copy(baseUrl=value)}}
 fun inspect(){job?.cancel();job=viewModelScope.launch{mutable.update{it.copy(message="TODO L01-G/L02-G: свържете repository с immutable UI state")}}}
 fun cancel(){job?.cancel();job=null}
 // TODO L01-I: собствен reducer и bounded transition history.
}

