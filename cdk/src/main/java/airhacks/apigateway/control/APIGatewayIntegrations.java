package airhacks.apigateway.control;

import java.util.List;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.CorsHttpMethod;
import software.amazon.awscdk.services.apigatewayv2.CorsPreflightOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.lambda.IFunction;
import software.constructs.Construct;

public interface APIGatewayIntegrations {


     
   
    /**
     * https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api.html
     */
    static void integrateWithHTTPApiGateway(Construct scope,IFunction function,List<String> allowOrigin) {
        var lambdaIntegration = HttpLambdaIntegration.Builder.create("HttpApiGatewayIntegration", function).build();
        var httpApiGateway = HttpApi.Builder.create(scope, "HttpApiGatewayIntegration")
                .defaultIntegration(lambdaIntegration)
                .corsPreflight(CorsPreflightOptions.builder()
                        .allowOrigins(allowOrigin)
                        .allowMethods(List.of(
                                CorsHttpMethod.GET,
                                CorsHttpMethod.POST,
                                CorsHttpMethod.PUT,
                                CorsHttpMethod.DELETE,
                                CorsHttpMethod.OPTIONS))
                        .allowHeaders(List.of("Authorization", "Content-Type"))
                        .allowCredentials(true)
                        .maxAge(Duration.seconds(300))
                        .build())
                .build();
        var url = httpApiGateway.getUrl();
        CfnOutput.Builder.create(scope, "HttpApiGatewayUrlOutput").value(url).build();
        CfnOutput.Builder.create(scope, "HttpApiGatewayCurlOutput").value("curl -i " + url + "hello").build();
    }

}
