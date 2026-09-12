package github.unit;

import github.clients.IssueClient;
import github.testdata.RepositoryTestData;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IssueClientTest {
    IssueClient issueClient = new IssueClient();

    @Test
    void should_reject_invalid_issue_state() {
        assertThrows(
                IllegalArgumentException.class,
                ()->issueClient.getIssuesByState(
                        RepositoryTestData.OCTOCAT_REPOSITORY_OWNER,
                        RepositoryTestData.EXISTING_REPOSITORY,
                        "invalidState"
                ));
    }
}
