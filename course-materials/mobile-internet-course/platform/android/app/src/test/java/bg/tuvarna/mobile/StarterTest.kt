package bg.tuvarna.mobile
import org.junit.Test
import org.junit.Assert.*
class StarterTest {
 @Test fun networkSnapshotRetainsSeparateCapabilities(){val s=NetworkSnapshot("1",setOf("WIFI"),true,false,true,false);assertTrue(s.internet);assertFalse(s.validated);assertTrue(s.metered)}
 @Test fun operationIdentitySurvivesRetryCopy(){val op=PendingOp("same-operation","local-1","{}");assertEquals(op.operationId,op.copy(attempt=1).operationId)}
}

