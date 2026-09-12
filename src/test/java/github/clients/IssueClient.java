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

    public Response getIssueByIssueNumber(String owner, String repository, int issueNumber) {
        return given().spec(RequestSpec.getRequestSpec())
                .when()
                .get("/repos/{owner}/{repository}/issues/{issueNumber}",
                        owner, repository, issueNumber);
    }

    public Response createIssue(String owner, String repository, String title) {
        return given().spec(RequestSpec.getRequestSpec())
                .body("""
                        {"title" : "%s" }
                        """.formatted(title))
                .when()
                .post("/repos/{owner}/{repository}/issues", owner, repository);
    }

    public Response closeIssue(String owner, String repo, int issueNumber) {
        return given().spec(RequestSpec.getRequestSpec())
                .body("""
                        { "state" : "closed" }
                        """)
                .when()
                .patch("/repos/{owner}/{repo}/issues/{issueNumber}", owner, repo, issueNumber);
    }
}
