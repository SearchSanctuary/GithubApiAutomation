package github.integration;
import github.assertions.ApiAssertions;
import github.models.Repository;
import github.testdata.RepositoryTestData;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import github.clients.RepositoryClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RepositoryApiTest {

    public final RepositoryClient repositoryClient = new RepositoryClient();
    @Test
    @Tag("integration")
    void should_return_github_repository() {

        Response response = repositoryClient.getRepository(
                        RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                        RepositoryTestData.EXISTING_REPOSITORY);

        Repository repository = response.as(Repository.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(repository.getName(), equalTo("Hello-World"));
        response.then().log().all();
    }

    @Test
    @Tag("integration")
    @Tag("negative")
    void should_return_404_for_non_existent_repository() {
        Response response = repositoryClient.getRepository(
                RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                RepositoryTestData.NON_EXISTING_REPOSITORY);

        assertThat(response.getStatusCode(), equalTo(404));
    }

    @Test
    @Tag("integration")
    void should_return_public_repository() {
        Response response = repositoryClient
                .getRepository("octocat", "Hello-World");
        Repository repository = response.as(Repository.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(repository.isPrivateRepository(), equalTo(false));
    }

    @Test
    @Tag("integration")
    void should_return_correct_repository_owner() {
        Response response = repositoryClient
                .getRepository("octocat", "Hello-World");
        Repository repository = response.as(Repository.class);

        ApiAssertions.assertStatusCode(response, 200);
        assertThat(repository.getOwner().getLogin(), equalTo("octocat"));
    }


}
