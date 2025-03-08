package com.culturespot.culturespotdomain.core.config;

import com.culturespot.culturespotdomain.core.storage.property.MinioProperty;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(MinioProperty.class)
public class MinioConfig {
    private final MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(
                        minioProperty.endpoint())
                .credentials(
                        minioProperty.credentials().accessKey(),
                        minioProperty.credentials().accessKey())
                .build();
    }
}
