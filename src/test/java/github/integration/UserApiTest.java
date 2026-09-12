package github.integration;

import github.assertions.ApiAssertions;
import github.clients.UserClient;
import github.models.Repository;
import github.models.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserApiTest {

    private final UserClient userClient = new UserClient();

    @Test
    void should_return_authenticated_user() {
        Response response = userClient.getAuthenticatedUser();
        ApiAssertions.assertSuccessful(response);
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));

        User user = response.as(User.class);
        assertThat(user.getLogin(), is(not(emptyOrNullString())));
    }

    @Test
    void should_return_authenticated_user_repositories() {
        Response response = userClient.getAuthenticatedUserRepositories();
        ApiAssertions.assertSuccessful(response);

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/repositories-schema.json"));

        List<Repository> repositories = response.jsonPath().getList("", Repository.class);
        assertThat(repositories, is(not(empty())));
        assertThat(repositories.get(0).getName(), is(not(emptyOrNullString())));
    }

    @Test
    void should_return_public_authenticated_user_repositories() {
        Response response = userClient.getAuthenticatedUserRepositories("public");
        ApiAssertions.assertSuccessful(response);

        List<Repository> repositories = response.jsonPath().getList("", Repository.class);
        assertThat(repositories, is(not(empty())));

        for (Repository repo : repositories) {
            assertThat(repo.isPrivateRepository(), equalTo(false));
        }
    }


}
