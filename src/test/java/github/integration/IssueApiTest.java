package github.integration;

import github.assertions.ApiAssertions;
import github.clients.IssueClient;
import github.models.Issue;
import github.testdata.RepositoryTestData;
import github.utils.AllureAttachments;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyString;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IssueApiTest {
    public final IssueClient issueClient = new IssueClient();

    @Test
    @Tag("integration")
    void should_return_issue() {
        Response response = issueClient.getIssues(
                RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                RepositoryTestData.EXISTING_REPOSITORY
        );

        List<Issue> issues = response.jsonPath().getList("", Issue.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(issues, is(not(empty())));
        assertThat(issues.getFirst().getTitle(), not(emptyString()));
        response.then().log().all();
    }

    @Test
    @Tag("integration")
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
    @Tag("integration")
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
    @Tag("integration")
    @Tag("negative")
    void should_return_404_for_nonexistent_issue() {
        Response response = issueClient.getIssueByIssueNumber(
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.API_TEST_REPO,
                99999999
        );
        AllureAttachments.attachResponse(response.asPrettyString());
        ApiAssertions.assertStatusCode(response, 404);
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
        assertThat(response.jsonPath().getString("message"), equalTo("Not Found"));
    }

    @Execution(ExecutionMode.SAME_THREAD)
    @Nested
    @Tag("lifecycle")
    class IssueLifecycleTests {

        private int createdIssueNumber;
        private boolean issueNeedsCleanup;

        @Step("Create test issue")
        Response createIssue() {
            Response response = issueClient.createIssue(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    "SDET ApIAutomationTestIssue");
            Issue issue = response.as(Issue.class);

            createdIssueNumber = issue.getNumber();
            issueNeedsCleanup = true;

            return response;
        }

        @Step("Close issue")
        Response closeIssue() {
            return issueClient.closeIssue(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    createdIssueNumber
            );
        }

        @Step("Retrieve created issue")
        Response getIssue() {
            return issueClient.getIssueByIssueNumber(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    createdIssueNumber
            );
        }

        @Step("Reopen issue")
        Response reopenIssue() {
            return issueClient.reopenIssue(
                    RepositoryTestData.REPOSITORY_OWNER,
                    RepositoryTestData.API_TEST_REPO,
                    createdIssueNumber
            );
        }

        @Severity(SeverityLevel.CRITICAL)
        @Description("Creates an issue in Github and verifies its details")
        @Test
        void should_create_issue() {
            Response response = createIssue();
            Issue issue = response.as(Issue.class);

            ApiAssertions.assertStatusCode(response, 201);

            assertThat(issue.getNumber(), is(greaterThan(0)));
            assertThat(issue.getTitle(), equalTo("SDET ApIAutomationTestIssue"));
            assertThat(issue.getState(), equalTo("open"));
        }

        @Test
        void should_close_issue() {
            createIssue();
            Response closeResponse = closeIssue();
            ApiAssertions.assertStatusCode(closeResponse, 200);

            Response response = getIssue();
            ApiAssertions.assertStatusCode(response, 200);

            Issue closedIssue = response.as(Issue.class);
            assertThat(closedIssue.getState(), equalTo("closed"));

            issueNeedsCleanup = false;
        }

        @Test
        void should_reopen_issue() {
            createIssue();
            Response closeResponse = closeIssue();
            ApiAssertions.assertStatusCode(closeResponse, 200);

            Response reopenResponse = reopenIssue();
            ApiAssertions.assertStatusCode(reopenResponse, 200);

            Response response = getIssue();
            ApiAssertions.assertStatusCode(response, 200);

            Issue reopenedIssue = response.as(Issue.class);
            assertThat(reopenedIssue.getState(), equalTo("open"));
        }

        @AfterEach
        void tearDown() {
            if (createdIssueNumber > 0 && issueNeedsCleanup) {
                Response cleanupResponse = closeIssue();
                ApiAssertions.assertStatusCode(cleanupResponse, 200);

                Response finalResponse = getIssue();
                ApiAssertions.assertStatusCode(finalResponse, 200);

                Issue finalIssue = finalResponse.as(Issue.class);
                assertThat(finalIssue.getState(), equalTo("closed"));
            }
        }
    }


}
