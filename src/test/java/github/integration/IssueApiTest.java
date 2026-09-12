package github.integration;

import github.assertions.ApiAssertions;
import github.clients.IssueClient;
import github.models.Issue;
import github.testdata.RepositoryTestData;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IssueApiTest {
    public final IssueClient issueClient = new IssueClient();

    @Test
    void should_return_issue() {
        Response response = issueClient.getIssues(
                RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY
        );

        List<Issue> issues = response.jsonPath().getList("", Issue.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(issues, is(not(empty())));
        assertThat(issues.get(0).getTitle(), not(emptyString()));
        response.then().log().all();
    }

    @Test
    void should_return_open_issues() {
        Response response = issueClient.getIssuesByState(
                RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY,
                "open"
        );
        List<Issue> issues = response.jsonPath().getList("", Issue.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(issues, is(not(empty())));

        for (Issue issue : issues) {
            assertThat(issue.getState(), equalTo("open"));
        }
    }

    @Test
    void should_return_closed_issues() {
        Response response = issueClient.getIssuesByState(
                RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY,
                "closed"
        );

        ApiAssertions.assertStatusCode(response, 200);
        List<Issue> issues = response.jsonPath().getList("", Issue.class);
        assertThat(issues, is(not(empty())));

        for (Issue issue : issues) {
            assertThat(issue.getState(), equalTo("closed"));
        }
    }

    @Test
    void should_create_issue() {
        Response response = issueClient.createIssue(
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.API_TEST_REPO,
                "SDET ApIAutomationTestIssue");
        Issue issue = response.as(Issue.class);

        try {
            ApiAssertions.assertStatusCode(response, 201);

            assertThat(issue.getNumber(), is(greaterThan(0)));
            assertThat(issue.getTitle(), equalTo("SDET ApIAutomationTestIssue"));
            assertThat(issue.getState(), equalTo("open"));

        } finally {
            Response cleanupResponse = issueClient.closeIssue(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    issue.getNumber()
            );

            ApiAssertions.assertStatusCode(cleanupResponse, 200);

            Response finalResponse = issueClient.getIssueByIssueNumber(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    issue.getNumber()
            );

            ApiAssertions.assertStatusCode(finalResponse, 200);

            Issue finalIssue = finalResponse.as(Issue.class);
            assertThat(finalIssue.getState(), equalTo("closed"));
        }

    }
}
