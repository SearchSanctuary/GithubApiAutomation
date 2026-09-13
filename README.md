# GitHub API Automation Framework

A Java-based API test automation framework built around the GitHub REST API. I put this together as a portfolio project to show practical experience in API automation, framework design, test organisation, CI/CD, and writing tests that are actually maintainable over time.

## Tech Stack

* Java 21
* Maven
* JUnit 5
* REST Assured
* AssertJ
* Jackson
* JSON Schema validation
* Allure JUnit 5
* GitHub Actions

## What's Covered

### User API

* Retrieve the authenticated user
* Retrieve repositories belonging to the authenticated user
* Filter repositories by visibility
* Validate response status codes
* Validate response structure using JSON Schema
* Deserialize API responses into Java models
* Validate invalid visibility input

### Repository API

* Retrieve a repository by owner and repository name
* Validate repository details
* Validate repository visibility
* Test non-existent repositories as negative scenarios

### Issue API

* Retrieve issues
* Retrieve issues by state
* Retrieve individual issues
* Validate invalid issue-state input
* Test non-existent issues
* Create issues
* Close issues
* Reopen issues
* Verify issue state changes
* Clean up test data after lifecycle tests

## Framework Architecture

The framework keeps API request logic separate from test logic through dedicated API client classes:

```text
Test
  |
  v
API Client
  |
  v
Request Specification
  |
  |-- Base URL
  |-- Authentication
  +-- REST Assured configuration
  |
  v
GitHub REST API
  |
  v
Response
  |
  |-- Status assertions
  |-- Schema validation
  +-- Java model deserialization
```

### API Clients

`UserClient`, `RepositoryClient`, and `IssueClient` handle the endpoint-specific HTTP request logic. Keeping this out of the test classes means the tests can focus purely on behaviour and assertions instead of repeating request setup everywhere.

### Request Specification

The shared request specification centralises:

* GitHub API base URL
* Authentication
* REST Assured configuration
* Error logging

The GitHub token is read from an environment variable, so it's never stored in the repository itself.

### Models

Jackson deserializes API responses into Java objects such as:

* `User`
* `Repository`
* `Issue`
* `Owner`

This means tests validate against strongly typed Java objects rather than digging through raw JSON.

## Project Structure

```text
src/test/java/github/
├── assertions/
│   └── ApiAssertions.java
├── clients/
│   ├── IssueClient.java
│   ├── RepositoryClient.java
│   └── UserClient.java
├── config/
│   ├── RequestSpec.java
│   └── TestConfig.java
├── integration/
│   ├── IssueApiTest.java
│   ├── RepositoryApiTest.java
│   └── UserApiTest.java
├── models/
│   ├── Issue.java
│   ├── Owner.java
│   ├── Repository.java
│   └── User.java
├── testdata/
│   └── RepositoryTestData.java
├── unit/
│   ├── IssueClientTest.java
│   └── UserClientTest.java
└── utils/
    └── AllureAttachments.java

src/test/resources/
├── junit-platform.properties
└── schemas/
    ├── error-schema.json
    ├── repositories-schema.json
    └── user-schema.json
```

## Test Categories

JUnit tags separate the different types of testing:

| Tag           | Purpose                                                  |
| ------------- | -------------------------------------------------------- |
| `unit`        | Client-side validation that doesn't require an API call |
| `integration` | Tests that interact with the GitHub REST API             |
| `negative`    | Invalid input and expected API error scenarios           |
| `lifecycle`   | Stateful create, update and retrieve workflows           |

Maven currently defaults to:

```xml
<test.tags>integration | unit | negative</test.tags>
```

So the standard command runs the main suite:

```bash
mvn test
```

Lifecycle tests are kept separate on purpose and run explicitly, since they change state.

## Running Tests

### Main test suite

```bash
mvn test
```

Runs integration, unit, and negative tests.

### Unit tests only

```bash
mvn test -Dtest.tags=unit
```

### Integration tests only

```bash
mvn test -Dtest.tags=integration
```

### Negative tests only

```bash
mvn test -Dtest.tags=negative
```

### Lifecycle tests

```bash
mvn test -Dtest.tags=lifecycle
```

These modify GitHub test data directly, which is why they're excluded from the default run.

## API Contract Validation

JSON Schema validation runs alongside status-code assertions to check the structure of key API responses. Right now schemas cover:

* Authenticated user responses
* Repository responses
* Error responses

This adds a layer of validation beyond just checking for a successful HTTP status.

## Negative Testing

The framework covers negative testing at both the client and API level, including:

* Invalid repository visibility
* Invalid issue state
* Non-existent repositories
* Non-existent issues
* Error response schema validation

Client-side validation catches invalid input before it even reaches the API where that makes sense.

## Stateful Lifecycle Testing

The issue lifecycle tests go beyond read-only calls and actually test state changes. A typical flow:

1. Create an issue
2. Verify the created issue
3. Close the issue
4. Verify the issue is closed
5. Reopen the issue
6. Verify the issue is open
7. Clean up the test data

Because these tests share state, they run on the same thread so concurrent lifecycle operations don't interfere with each other.

## Parallel Test Execution

JUnit 5 parallel execution is enabled for tests that don't depend on shared state:

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```

Independent tests run concurrently while the stateful lifecycle tests stay protected from parallel execution.

## Authentication and Secrets

The framework reads the GitHub API token from:

```text
GITHUB_TOKEN
```

It's never hard-coded into the framework or committed to source control.

In GitHub Actions, the token comes from a repository secret and gets exposed to the tests as the `GITHUB_TOKEN` environment variable.

## CI/CD

GitHub Actions runs the tests automatically. The workflow:

1. Checks out the repository
2. Sets up Java 21 using Temurin
3. Enables Maven dependency caching
4. Provides the GitHub API token through a repository secret
5. Runs the Maven test suite

It triggers on pushes and pull requests, so changes get automated feedback as soon as they're submitted.

## Test Reporting

Allure JUnit 5 is wired in for test reporting, using metadata like:

* Test descriptions
* Severity
* Test steps
* Response attachments

This gives more context when digging into a failed test.

## Design Decisions

**Separate API clients from tests.** HTTP request logic lives in dedicated client classes, cutting duplication and letting the same endpoint logic get reused across multiple tests.

**Use shared request configuration.** Things like the base URL and authentication are defined once instead of repeated throughout individual tests.

**Validate inputs before making requests.** Unsupported values, like invalid issue states or repository visibility, get rejected client-side before a request is even sent.

**Test against the real API.** Integration tests hit the actual GitHub API rather than mocking it, so the framework exercises real HTTP requests, authentication, and response handling.

**Keep state-changing tests separate.** Lifecycle tests modify GitHub data, so they're left out of the default run to avoid unintended side effects during normal testing.

**Support parallel execution.** Independent tests run concurrently to cut down overall run time, while stateful tests are isolated where it matters.

## Project Purpose

This project is meant to demonstrate hands-on SDET and test automation experience across:

* REST API automation
* Java
* REST Assured
* JUnit 5
* Framework design
* Reusable API clients
* Unit testing
* Integration testing
* Negative testing
* JSON Schema validation
* API response deserialization
* Stateful API workflows
* Test-data cleanup
* Parallel test execution
* Secure authentication
* CI/CD
* Allure test reporting
