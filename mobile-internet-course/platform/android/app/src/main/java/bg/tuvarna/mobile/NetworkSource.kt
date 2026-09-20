package bg.tuvarna.mobile
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
data class NetworkSnapshot(val networkId:String?,val transports:Set<String>,val internet:Boolean,val validated:Boolean,val metered:Boolean,val vpn:Boolean)
interface NetworkSource { fun snapshots():Flow<NetworkSnapshot> }
class AndroidNetworkSource(private val manager:ConnectivityManager):NetworkSource {
 override fun snapshots():Flow<NetworkSnapshot> = flowOf(NetworkSnapshot(null,emptySet(),false,false,true,false))
 // TODO L01-G: callbackFlow + registerDefaultNetworkCallback; capabilities snapshot; unregister в awaitClose.
}

