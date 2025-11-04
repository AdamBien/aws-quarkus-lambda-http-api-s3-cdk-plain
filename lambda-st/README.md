# System Tests

End-to-end tests against deployed API.

## Running Tests

```bash
export BASE_URI=https://[GENERATED_ID].execute-api.eu-central-1.amazonaws.com
mvn clean test-compile failsafe:integration-test
```