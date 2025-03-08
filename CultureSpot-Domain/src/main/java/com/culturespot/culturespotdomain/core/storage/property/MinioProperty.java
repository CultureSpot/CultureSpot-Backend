package com.culturespot.culturespotdomain.core.storage.property;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
@ConfigurationProperties(prefix = "spring.minio")
public record MinioProperty(
        String endpoint,
        String bucketName,
        Credentials credentials
) {
    public record Credentials(String accessKey, String secretKey) {
    }
}