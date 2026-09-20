package bg.tuvarna.mobile
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
class MainActivity:ComponentActivity(){ override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);enableEdgeToEdge();setContent{
 val vm:LabViewModel=viewModel();val state by vm.state.collectAsStateWithLifecycle()
 MaterialTheme{Scaffold{padding->Column(Modifier.padding(padding).padding(16.dp)){
 Text("Mobile Activity Platform",style=MaterialTheme.typography.headlineSmall)
 Text(state.message)
 OutlinedTextField(state.baseUrl,{vm.baseUrl(it)},label={Text("Единен base URL")})
 Button(onClick=vm::inspect){Text("Наблюдение / заявка")}
 Button(onClick=vm::cancel){Text("Отказ")}
 }}}
 }} }

