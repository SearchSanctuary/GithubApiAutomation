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
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY
        );

        List<Issue> issues = response.jsonPath().getList("", Issue.class);

        ApiAssertions.assertSuccessful(response);
        assertThat(issues, is(not(empty())));
        assertThat(issues.get(0).getTitle(), not(emptyString()));
        response.then().log().all();
    }

    @Test
    void should_return_open_issues() {
        Response response = issueClient.getIssuesByState(
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY,
                "open"
        );
        List<Issue> issues = response.jsonPath().getList("", Issue.class);

        ApiAssertions.assertSuccessful(response);
        assertThat(issues, is(not(empty())));

        for (Issue issue : issues) {
            assertThat(issue.getState(), equalTo("open"));
        }
    }

    @Test
    void should_return_closed_issues() {
        Response response = issueClient.getIssuesByState(
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY,
                "closed"
        );

        ApiAssertions.assertSuccessful(response);
        List<Issue> issues = response.jsonPath().getList("", Issue.class);
        assertThat(issues, is(not(empty())));

        for (Issue issue : issues) {
            assertThat(issue.getState(), equalTo("closed"));
        }
    }
}
