package github.config;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {
    public static RequestSpecification getRequestSpec() {

        RequestSpecBuilder builder = new RequestSpecBuilder().setBaseUri(TestConfig.BASE_URL);

        if (TestConfig.GITHUB_TOKEN != null && !TestConfig.GITHUB_TOKEN.isBlank()){
            builder.addHeader(
                    "Authorization",
                    "Bearer " + TestConfig.GITHUB_TOKEN);
        }

        return builder.build();
    }
}
