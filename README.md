# AWS Quarkus Lambda HTTP API with S3

AWS Lambda with Quarkus/MicroProfile providing RESTful HTTP API backed by S3. Infrastructure provisioned with AWS CDK in Java.

## Features

- Full CRUD operations for address management
- Pagination support
- Partial field updates
- Zero external dependencies beyond AWS SDK and Quarkus

## Structure

- `lambda/` - Quarkus application with BCE-structured business logic
- `cdk/` - AWS infrastructure definition
- `lambda-st/` - System tests (e2e)

Code follows BCE pattern: https://bce.design

## Build and Deploy

```bash
./buildAndDeployDontAsk.sh
```

Or manually:

```bash
cd lambda && mvn clean package
cd ../cdk && mvn clean package && cdk deploy
```

## Testing

```bash
# Unit tests
cd lambda && mvn test

# System tests
cd lambda-st && mvn verify
```
