package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.ws.rs.core.*; import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
@Path("/fixture") public class FixtureResource {
 private final AtomicInteger sequence=new AtomicInteger();
 @GET @Path("/activities") public Response read(@QueryParam("mode") @DefaultValue("ok") String mode) throws InterruptedException {
  int n=sequence.getAndIncrement()%10;
  if(mode.equals("mixed")) mode=n<6?"ok":n<8?"server":"slow";
  if(mode.equals("slow")) Thread.sleep(3000);
  if(mode.equals("server")) return Response.status(503).header("Retry-After","1").entity(Map.of("code","TEMPORARY","message","Injected failure")).build();
  if(mode.equals("client")) return Response.status(422).entity(Map.of("code","INVALID","message","Injected invalid input")).build();
  if(mode.equals("malformed")) return Response.ok("{broken",MediaType.APPLICATION_JSON).build();
  return Response.ok(Map.of("items",List.of(Map.of("id","a1","userId","u1","title","Demo activity","status","CREATED","version",1)),"nextCursor","")).build();
 }
}
