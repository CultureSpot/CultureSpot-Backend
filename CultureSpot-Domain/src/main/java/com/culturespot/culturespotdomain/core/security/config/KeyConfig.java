package com.culturespot.culturespotdomain.core.security.config;

import com.culturespot.culturespotdomain.core.security.jwt.builder.KeyBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class KeyConfig {

    @Value("${spring.jwt.public-key-pem}")
    private Resource publicKeyFile;

    @Value("${spring.jwt.private-key-pem}")
    private Resource privateKeyFile;

    @Bean
    public PublicKey publicKey() throws Exception {
        String base64PublicKey = readKeyFromFile(publicKeyFile);
        return KeyBuilder.builder()
                .setPublicKey(base64PublicKey)
                .build()
                .getPublicKey();
    }

    @Bean
    public PrivateKey privateKey() throws Exception {
        String base64PrivateKey = readKeyFromFile(privateKeyFile);
        return KeyBuilder.builder()
                .setPrivateKey(base64PrivateKey)
                .build()
                .getPrivateKey();
    }

    /**
     * 파일에서 Base64로 인코딩된 키 값을 읽어오는 메서드
     */
    private String readKeyFromFile(Resource resource) throws Exception {
        byte[] keyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return new String(keyBytes, StandardCharsets.UTF_8);
    }
}


