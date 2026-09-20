package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.ws.rs.core.Response;
@Path("/notifications") public class NotificationsResource {
 @GET public Response get(){return Contract.todo("L04-I: определете собствен contract/data ownership и един remote call");}
}
