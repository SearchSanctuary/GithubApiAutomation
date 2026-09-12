package github.clients;

import github.config.RequestSpec;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {
    public Response getAuthenticatedUser() {
        return given().spec(RequestSpec.getRequestSpec())
                .when().get("/user");
    }

    public Response getAuthenticatedUserRepositories() {
        return given().spec(RequestSpec.getRequestSpec())
                .when().get("/user/repos");
    }

    public Response getAuthenticatedUserRepositories(String visibility) {
        if (!visibility.equals("public") && !visibility.equals("private") && !visibility.equals("all")){
            throw new IllegalArgumentException("Invalid visibility");
        }
        return given().spec(RequestSpec.getRequestSpec())
                .queryParam("visibility", visibility)
                .when().get("/user/repos");
    }
}

