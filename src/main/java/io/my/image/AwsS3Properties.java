package io.my.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("aws")
public class AwsS3Properties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
