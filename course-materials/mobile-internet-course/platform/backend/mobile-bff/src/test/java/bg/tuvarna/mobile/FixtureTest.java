package bg.tuvarna.mobile;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
@QuarkusTest class FixtureTest {
 @Test void fixtureDistinguishesStatusAndDecodeFailures(){
  given().get("/fixture/activities?mode=ok").then().statusCode(200).body("items.size()",is(1));
  given().get("/fixture/activities?mode=client").then().statusCode(422);
  given().get("/fixture/activities?mode=server").then().statusCode(503).header("Retry-After","1");
  given().get("/fixture/activities?mode=malformed").then().statusCode(200).body(equalTo("{broken"));
 }
}
