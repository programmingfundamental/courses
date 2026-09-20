package bg.tuvarna.mobile;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@Path("/unstable") public class UnstableResource {
 private final AtomicInteger count = new AtomicInteger();
 @GET public Response get(@QueryParam("mode") @DefaultValue("mixed") String mode) throws InterruptedException {
  int slot = count.getAndIncrement() % 10;
  if (mode.equals("mixed")) mode = slot < 6 ? "ok" : slot < 8 ? "fail" : "slow";
  if (mode.equals("slow")) Thread.sleep(3000);
  if (mode.equals("fail")) return Response.status(503).entity(Map.of("code", "TEMPORARY")).build();
  return Response.ok(Map.of("suggestion", "Кратка учебна активност", "source", "live")).build();
 }
}
