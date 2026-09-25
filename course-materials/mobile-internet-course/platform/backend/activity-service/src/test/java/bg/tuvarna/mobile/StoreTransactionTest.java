package bg.tuvarna.mobile;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest class StoreTransactionTest {
 @Inject ActivityStore store;
 @Test void rolledBackInsertLeavesNoActivity() {
  String id=UUID.randomUUID().toString();
  assertThrows(SQLException.class,()->store.inTransaction(c->{
   store.insert(c,new ActivityStore.Activity(id,"u1","Rollback fixture","CREATED",1,1));
   throw new SQLException("Injected before commit");
  }));
  assertTrue(store.find(id).isEmpty());
 }
 @Test void generatedIdentityIsReadable() {
  var activity=store.insert("u1","Starter storage fixture");
  assertEquals(activity,store.find(activity.id()).orElseThrow());
 }
}
