# System Tests

End-to-end tests verifying the deployed Lambda API against AWS infrastructure.

## Running Tests

```bash
export BASE_URI=https://[GENERATED_ID].execute-api.eu-central-1.amazonaws.com
mvn clean verify
```

## Configuration

Tests use MicroProfile REST Client with `base_uri` config key. The BASE_URI environment variable must point to the deployed API Gateway endpoint.