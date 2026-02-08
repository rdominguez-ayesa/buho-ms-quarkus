package es.ayesa.buho.microservice.administracion.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UsuarioResourceTest {

    @Test
    void balanceador_dummy_ok() {
        given()
          .when().get("/balanceador/dummy")
          .then()
            .statusCode(200)
            .body(containsString("En dummy"));
    }
}
