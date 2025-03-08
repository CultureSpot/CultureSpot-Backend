package com.culturespot.culturespotdomain.core.config;

import com.culturespot.culturespotdomain.core.storage.property.MinioProperty;
import com.culturespot.culturespotdomain.core.storage.service.MinioStorageService;
import com.culturespot.culturespotdomain.core.storage.service.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

@Configuration
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "minio")
    public StorageService minioStorageService(MinioClient minioClient, MinioProperty property) {
        return new MinioStorageService(minioClient, property);
    }
}
