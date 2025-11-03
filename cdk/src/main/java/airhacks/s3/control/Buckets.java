package airhacks.s3.control;

import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

public interface Buckets {

    static Bucket createAddressesBucket(Construct scope) {
        var bucket =  Bucket.Builder.create(scope, "AddressesBucket")
                .build();
        Tags.of(bucket).add("BucketName", "AddressesBucket");
        return bucket;
    }
}
