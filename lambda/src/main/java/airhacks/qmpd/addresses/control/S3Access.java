package airhacks.qmpd.addresses.control;

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Provides static access to the S3Client for address storage operations.
 * The client is configured from the AWS_REGION environment variable.
 */
interface S3Access {
    String defaultRegion = "eu-central-1";
    
    S3Client CLIENT = S3Client.builder()
        .region(Region.of(System.getenv().getOrDefault("AWS_REGION", defaultRegion)))
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
}
