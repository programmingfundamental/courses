package bg.tuvarna.mobile;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
@QuarkusTest class HealthTest { @Test void serverStarts() { given().when().get("/q/health/live").then().statusCode(200); } }

