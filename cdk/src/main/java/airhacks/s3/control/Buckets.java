package airhacks.s3.control;

import airhacks.Configuration;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

/**
 * Factory methods for creating S3 buckets.
 */
public interface Buckets {

    static Bucket createAddressesBucket(Construct scope, Configuration configuration) {
        return Bucket.Builder.create(scope, "AddressesBucket")
                .bucketName(configuration.addressBucketName())
                .build();
    }
}
