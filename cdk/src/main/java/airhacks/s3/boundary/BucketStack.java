package airhacks.s3.boundary;

import airhacks.Configuration;
import airhacks.ConventionalDefaults;
import airhacks.s3.control.Buckets;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Tags;
import software.constructs.Construct;

/**
 * CDK stack for provisioning S3 bucket for address storage.
 */
public class BucketStack extends Stack {

    public BucketStack(Construct scope, Configuration configuration) {
        super(scope, ConventionalDefaults.stackName(configuration.appName(), "bucket"),
                configuration.stackProperties());

        var bucket = Buckets.createAddressesBucket(this, configuration);
        Tags.of(bucket).add("BucketName", configuration.addressBucketName());
    }
}
