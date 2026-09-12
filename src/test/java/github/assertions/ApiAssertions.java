package github.assertions;

import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApiAssertions {
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode(), equalTo(expectedStatusCode));
    }

}
