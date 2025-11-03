package airhacks.s3.control;

import airhacks.Configuration;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

public interface Buckets {

    static Bucket createAddressesBucket(Construct scope, Configuration configuration) {
        var bucket =  Bucket.Builder.create(scope, "AddressesBucket")
                .build();
        Tags.of(bucket).add("BucketName", configuration.addressBucketName());
        return bucket;
    }
}
