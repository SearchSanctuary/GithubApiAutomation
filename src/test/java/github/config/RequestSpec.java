package github.config;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.specification.RequestSpecification;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;

public class RequestSpec {
    public static RequestSpecification getRequestSpec() {

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(TestConfig.BASE_URL)
                .setConfig(RestAssured.config()
                        .logConfig(
                                LogConfig.logConfig().blacklistHeader("Authorisation")));

        if (TestConfig.GITHUB_TOKEN != null && !TestConfig.GITHUB_TOKEN.isBlank()){
            builder.addHeader(
                    "Authorization",
                    "Bearer " + TestConfig.GITHUB_TOKEN);
        }

        builder.addFilter(new ErrorLoggingFilter());

        return builder.build();
    }
}
