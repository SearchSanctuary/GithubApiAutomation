package github.clients;

import github.config.RequestSpec;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IssueClient {
    public Response getIssues(String owner, String repository) {
        return given().spec(RequestSpec.getRequestSpec())
                .when().get("/repos/{owner}/{repository}/issues", owner, repository);
    }

    public Response getIssuesByState(String owner, String repository, String state) {
        if(!state.equals("open") && !state.equals("closed")){
            throw new IllegalArgumentException(
                    "State must be 'open' or 'closed'");
        }

        return given().spec(RequestSpec.getRequestSpec())
                .queryParam("state", state)
                .when().get("/repos/{owner}/{repository}/issues", owner, repository);
    }
}
