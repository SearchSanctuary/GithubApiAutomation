package github.clients;

import github.config.RequestSpec;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RepositoryClient {

    public Response getRepository(String owner, String repository) {
        return given().spec(RequestSpec.getRequestSpec())
                .when().get("/repos/{owner}/{repository}", owner, repository);
    }
}
