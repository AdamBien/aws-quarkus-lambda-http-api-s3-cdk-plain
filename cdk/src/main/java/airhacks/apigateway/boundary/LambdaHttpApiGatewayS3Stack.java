package airhacks.apigateway.boundary;

import java.util.Map;

import airhacks.Configuration;
import airhacks.ConventionalDefaults;
import airhacks.apigateway.control.APIGatewayIntegrations;
import airhacks.lambda.control.QuarkusLambda;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

public class LambdaHttpApiGatewayS3Stack extends Stack {

    public LambdaHttpApiGatewayS3Stack(Construct scope, Configuration configuration) {
        super(scope, ConventionalDefaults.stackName(configuration.appName(), "lambda-http-api-s3"), configuration.stackProperties());
        var addressBucketName = configuration.bucketName();
        var addressBucket = Bucket.fromBucketName(this, "ImportedAddressBucket", addressBucketName);
        var envEntries = Map.<String,String>of(Configuration.addressBucketNameKeyAsEnvEntry,addressBucketName);
        var functionName = configuration.functionName();
        var quarkusLambda = new QuarkusLambda(this,functionName,envEntries);
        var function = quarkusLambda.getFunction();
        addressBucket.grantReadWrite(function);
        var allowOrigins = configuration.allowOrigins();
        APIGatewayIntegrations.integrateWithHTTPApiGateway(this, function, allowOrigins);
    }
}
