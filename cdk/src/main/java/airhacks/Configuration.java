package airhacks;

import java.util.List;

import airhacks.configuration.control.ZCfg;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public record Configuration(String appName) {

    public final static String addressTableNameKey = "address.table.name";
    public final static String addressTableNameKeyAsEnvEntry = addressTableNameKey
            .replace(".", "_")
            .toUpperCase();

    public final static String addressBucketNameKey = "address.bucket.name";
    public final static String addressBucketNameKeyAsEnvEntry = addressBucketNameKey
            .replace(".", "_")
            .toUpperCase();

    public StackProps stackProperties() {
        ZCfg.load(appName);
        var region = ZCfg.string("stack.props.region");
        var env = Environment
                .builder()
                .region(region)
                .build();
        return StackProps
                .builder()
                .env(env)
                .build();
    }

    public String functionName() {
        ZCfg.load(appName);
        return ZCfg.string("function.name");
    }

    public String addressTableName() {
        ZCfg.load(appName);
        return ZCfg.string(addressTableNameKey, "Addresses");
    }

    public String addressBucketName() {
        ZCfg.load(appName);
        return ZCfg.string(addressBucketNameKey, "addresses-bucket");
    }

    public List<String> allowOrigins() {
        ZCfg.load(appName);
        var origin = ZCfg.string("http.api.allow.origins");
        return List.of(origin);
    }
}
