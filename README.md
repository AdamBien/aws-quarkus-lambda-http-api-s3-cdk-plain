# AWS Quarkus Lambda HTTP API with S3

Quarkus Lambda providing RESTful HTTP API backed by S3. Infrastructure as code with AWS CDK.

## Modules

- [`lambda/`](lambda/) - Quarkus application (BCE pattern: https://bce.design)
- [`cdk/`](cdk/) - AWS infrastructure
- [`lambda-st/`](lambda-st/) - System tests

## Build and Deploy

```bash
./buildAndDeployDontAsk.sh
```

### References

- [BCE Architecture Design Pattern](https://bce.design/) - This project organizes code using the Boundary-Control-Entity pattern
- This project is a member of [Constructions Cloud](https://constructions.cloud/)

See you at: [airhacks.live](https://airhacks.live)
