package github.unit;

import github.clients.IssueClient;
import github.testdata.RepositoryTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IssueClientTest {
    IssueClient issueClient = new IssueClient();

    @Test
    void should_reject_invalid_issue_state() {
        assertThrows(
                IllegalArgumentException.class,
                ()->issueClient.getIssuesByState(
                        RepositoryTestData.REPOSITORY_OWNER,
                        RepositoryTestData.EXISTING_REPOSITORY,
                        "invalidState"
                ));
    }
}
