package airhacks.s3.boundary;

import airhacks.Configuration;
import airhacks.ConventionalDefaults;
import airhacks.s3.control.Buckets;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

/**
 * CDK stack for provisioning S3 bucket for address storage.
 */
public class BucketStack extends Stack {

    Bucket addressesBucket;

    public BucketStack(Construct scope, Configuration configuration) {
        super(scope, ConventionalDefaults.stackName(configuration.appName(), "bucket"),
                configuration.stackProperties());
        this.addressesBucket = Buckets.createAddressesBucket(this);
    }

    public String bucketName(){
        return this.addressesBucket.getBucketName();
    }
}
