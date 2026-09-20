package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.ws.rs.core.Response; import jakarta.inject.Inject;
import jakarta.validation.constraints.*;
@Path("/activities") public class ActivitiesResource {
 @Inject ActivityStore store;
 public record CreateActivity(@NotBlank @Size(max=40) String userId,@NotBlank @Size(max=120) String title) {}
 @GET public Response list(){return Contract.todo("L03-G: response DTO и GET contract");}
 @GET @Path("/{id}") public Response get(@PathParam("id") String id){return Contract.todo("L03-G: 200/404 contract");}
 @POST public Response create(CreateActivity input){return Contract.todo("L03-G: validation, 201, Location");}
 // TODO L03-I: /activities/search с filtering/pagination, без entity serialization.
 // TODO L04-G: проверка на user чрез UsersClient; downstream outage != user not found.
 // TODO L08-I: Idempotency-Key + request hash и transaction преди response.
 // TODO L09-G: status command + persisted version; публикуване на ActivityEvent.
}
