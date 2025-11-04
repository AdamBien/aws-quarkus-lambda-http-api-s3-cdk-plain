# Lambda Module

Quarkus-based AWS Lambda function providing RESTful address management API with S3

## Architecture

Built following BCE (Boundary-Control-Entity) pattern:

- **Boundary**: JAX-RS resources exposing HTTP endpoints
- **Control**: Business logic for validation and storage operations
- **Entity**: Domain objects and data transfer records

## Technology Stack

- Quarkus with Amazon Lambda HTTP support
- AWS SDK for S3
- Jakarta REST (JAX-RS) with JSON-P serialization
- JUnit 5 with AssertJ for testing

## Build

```bash
mvn clean package
```

## API Endpoints

- `POST /addresses` - Create address
- `GET /addresses/{id}` - Retrieve address by ID
- `PUT /addresses/{id}` - Update address (partial updates supported)
- `DELETE /addresses/{id}` - Remove address
