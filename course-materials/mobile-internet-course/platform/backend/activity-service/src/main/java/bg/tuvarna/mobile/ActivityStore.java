package bg.tuvarna.mobile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*; import java.util.*;
@ApplicationScoped public class ActivityStore {
 @Inject DataSource source;
 public record Activity(String id,String userId,String title,String status,long version,long createdAt) {}
 @PostConstruct void init() {
  try(var c=source.getConnection(); var st=c.createStatement()) {
   st.execute("CREATE TABLE IF NOT EXISTS activities(id VARCHAR(36) PRIMARY KEY,user_id VARCHAR(40) NOT NULL,title VARCHAR(120) NOT NULL,status VARCHAR(20) NOT NULL,version BIGINT NOT NULL,created_at BIGINT NOT NULL)");
   st.execute("CREATE TABLE IF NOT EXISTS request_ledger(principal_id VARCHAR(80),op_key VARCHAR(80),request_hash VARCHAR(64) NOT NULL,response_json CLOB NOT NULL,created_at BIGINT NOT NULL,PRIMARY KEY(principal_id,op_key))");
  } catch(SQLException e){throw new IllegalStateException("Schema initialization failed",e);}
 }
 public List<Activity> recent() {
  try(var c=source.getConnection(); var q=c.prepareStatement("SELECT * FROM activities ORDER BY created_at DESC,id LIMIT 50"); var r=q.executeQuery()) {
   var out=new ArrayList<Activity>(); while(r.next()) out.add(row(r)); return List.copyOf(out);
  } catch(SQLException e){throw new IllegalStateException(e);}
 }
 public Optional<Activity> find(String id) {
  try(var c=source.getConnection();var q=c.prepareStatement("SELECT * FROM activities WHERE id=?")) {q.setString(1,id);try(var r=q.executeQuery()){return r.next()?Optional.of(row(r)):Optional.empty();}}
  catch(SQLException e){throw new IllegalStateException(e);}
 }
 public Activity insert(String userId,String title) {
  var a=new Activity(UUID.randomUUID().toString(),userId,title,"CREATED",1,System.currentTimeMillis());
  try(var c=source.getConnection()){insert(c,a);return a;}catch(SQLException e){throw new IllegalStateException(e);}
 }
 public void insert(Connection c,Activity a)throws SQLException {
  try(var q=c.prepareStatement("INSERT INTO activities VALUES(?,?,?,?,?,?)")){q.setString(1,a.id());q.setString(2,a.userId());q.setString(3,a.title());q.setString(4,a.status());q.setLong(5,a.version());q.setLong(6,a.createdAt());q.executeUpdate();}
 }
 public Connection connection()throws SQLException{return source.getConnection();}
 @FunctionalInterface public interface SqlWork<T>{T apply(Connection c)throws SQLException;}
 public <T> T inTransaction(SqlWork<T> work)throws SQLException {
  try(var c=source.getConnection()){
   c.setAutoCommit(false);
   try{T result=work.apply(c);c.commit();return result;}
   catch(SQLException|RuntimeException e){c.rollback();throw e;}
  }
 }
 private Activity row(ResultSet r)throws SQLException{return new Activity(r.getString("id"),r.getString("user_id"),r.getString("title"),r.getString("status"),r.getLong("version"),r.getLong("created_at"));}
 // TODO L03-I: bounded filtered query със stable sort и validated pagination.
 // TODO L08-I: атомарен ledger+business insert; не използвайте in-memory dedup map.
}
