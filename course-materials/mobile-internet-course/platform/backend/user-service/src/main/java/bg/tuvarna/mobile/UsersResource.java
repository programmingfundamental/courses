package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import java.util.Map;
@Path("/users") public class UsersResource {
 @org.eclipse.microprofile.config.inject.ConfigProperty(name="lab.user-delay-ms",defaultValue="0") long delayMs;
 private final java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
 @GET @Path("/{id}") public Map<String,Object> user(@PathParam("id") String id) throws InterruptedException {
  if(calls.incrementAndGet()%3==0) Thread.sleep(Math.max(0,Math.min(delayMs,3000)));
  if(!id.equals("u1")) throw new NotFoundException();
  return Map.of("id",id,"displayName","Лабораторен потребител");
 }
}
