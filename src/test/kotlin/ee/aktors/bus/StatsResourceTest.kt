package ee.aktors.bus

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class StatsResourceTest {

    @Test
    fun testHelloEndpoint() {
        given()
          .`when`().get("/api/stats")
          .then()
             .statusCode(200)
             .body(`is`("hello"))
    }

}