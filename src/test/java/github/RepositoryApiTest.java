package github;
import github.assertions.ApiAssertions;
import github.clients.IssueClient;
import github.models.Issue;
import github.models.Repository;
import github.testdata.RepositoryTestData;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import github.clients.RepositoryClient;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RepositoryApiTest {

    public final RepositoryClient repositoryClient = new RepositoryClient();
    @Test
    void should_return_github_repository() {

        Response response = repositoryClient.getRepository(
                        RepositoryTestData.REPOSITORY_OWNER,
                        RepositoryTestData.EXISTING_REPOSITORY);

        Repository repository = response.as(Repository.class);

        ApiAssertions.assertSuccessful(response);
        assertThat(repository.getName(), equalTo("Hello-World"));
        response.then().log().all();
    }

    @Test
    void should_return_404_for_non_existent_repository() {
        Response response = repositoryClient.getRepository(
                RepositoryTestData.REPOSITORY_OWNER,
                RepositoryTestData.NON_EXISTING_REPOSITORY);

        assertThat(response.getStatusCode(), equalTo(404));
    }

    @Test
    void should_return_public_repository() {
        Response response = repositoryClient
                .getRepository("octocat", "Hello-World");
        Repository repository = response.as(Repository.class);

        ApiAssertions.assertSuccessful(response);
        assertThat(repository.isPrivateRepository(), equalTo(false));
    }

    @Test
    void should_return_correct_repository_owner() {
        Response response = repositoryClient
                .getRepository("octocat", "Hello-World");
        Repository repository = response.as(Repository.class);

        ApiAssertions.assertSuccessful(response);
        assertThat(repository.getOwner().getLogin(), equalTo("octocat"));
    }


}
