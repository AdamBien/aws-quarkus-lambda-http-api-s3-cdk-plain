package airhacks;

import java.util.List;

import airhacks.configuration.control.ZCfg;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public record Configuration(String appName,String bucketName) {

 public final static String addressBucketNameKey = "address.bucket.name";

     public final static String addressBucketNameKeyAsEnvEntry = addressBucketNameKey
            .replace(".", "_")
            .toUpperCase();


    public Configuration(String appName){
        this(appName, null);
    }

    Configuration withBucketName(String generatedName){
        return new Configuration(appName, generatedName);
    }

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

    public String functionName(String defaultName) {
        ZCfg.load(appName);
        return ZCfg.string("function.name",defaultName);
    }


    public List<String> allowOrigins() {
        ZCfg.load(appName);
        var origin = ZCfg.string("http.api.allow.origins");
        return List.of(origin);
    }
}
