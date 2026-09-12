package github.utils;

import io.qameta.allure.Attachment;

public class AllureAttachments {
    @Attachment(value = "API Response", type = "application/json")
    public static String attachResponse(String response) {
        return response;
    }
}
