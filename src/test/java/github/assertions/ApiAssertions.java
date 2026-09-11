package github.assertions;

import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApiAssertions {
    public static void assertSuccessful(Response response) {
        assertThat(response.getStatusCode(), equalTo(200));
    }
}
