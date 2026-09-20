package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.ws.rs.core.Response;
@Path("/mobile") public class HomeResource {
 @GET @Path("/home") public Response home(){return Contract.todo("L06-G: aggregate user + activities, explicit section status");}
 @POST @Path("/activities") public Response create(String body){return Contract.todo("L08-G: forward command + stable Idempotency-Key");}
 // TODO L06-I: различен screen DTO и partial-failure policy.
 // TODO L07-G: downstream policy в отделен CDI bean, time budget и ограничени retry.
 // TODO L10-G: role и ownership authorization; не доверявайте userId от body.
}
