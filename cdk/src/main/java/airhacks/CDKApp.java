package airhacks;

import airhacks.apigateway.boundary.LambdaHttpApiGatewayS3Stack;
import airhacks.s3.boundary.BucketStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;

public interface CDKApp {
    
    String appName = "quarkus-lambda-http-api-s3";

    static void main(String... args) {

        var app = new App();

        Tags.of(app).add("project", "Quarkus on AWS Lambda via HTTP Api and S3");
        Tags.of(app).add("environment", "development");
        Tags.of(app).add("application", appName);
        var configuration = new Configuration(appName);
        new BucketStack(app, configuration);
        new LambdaHttpApiGatewayS3Stack(app, configuration);

        app.synth();  
    }
}
