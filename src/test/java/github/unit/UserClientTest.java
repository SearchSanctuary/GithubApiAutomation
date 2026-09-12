package github.unit;

import github.clients.UserClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserClientTest {
    UserClient userClient = new UserClient();

    @Test
    void should_reject_invalid_visibility() {
        assertThrows(IllegalArgumentException.class,
                ()->userClient.getAuthenticatedUserRepositories("invalidVisibility"));
    }
}
