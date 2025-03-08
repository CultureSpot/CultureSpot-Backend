package com.culturespot.culturespotdomain.core.security.jwt.builder;

import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class KeyBuilder {
    private String privateKeyStr;
    private String publicKeyStr;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private KeyBuilder() {}

    public static KeyBuilder builder() {
        return new KeyBuilder();
    }

    public KeyBuilder setPrivateKey(String privateKeyStr) {
        this.privateKeyStr = privateKeyStr;
        return this;
    }

    public KeyBuilder setPublicKey(String publicKeyStr) {
        this.publicKeyStr = publicKeyStr;
        return this;
    }

    public KeyBuilder build() throws Exception {
        if (privateKeyStr != null && !privateKeyStr.isEmpty()) {
            this.privateKey = buildPrivateKey(privateKeyStr);
        }

        if (publicKeyStr != null && !publicKeyStr.isEmpty()) {
            this.publicKey = buildPublicKey(publicKeyStr);
        }

        return this;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    // PEM 형식에서 Base64 인코딩된 키 부분만 추출
    private byte[] extractKeyBytes(String key, String beginMarker, String endMarker) {
        return Optional.ofNullable(key)
                .map(str -> {
                    // 먼저 헤더와 푸터를 정규식으로 제거
                    String withoutHeaderFooter = str.replaceAll("-----BEGIN [A-Z ]+-----", "")
                            .replaceAll("-----END [A-Z ]+-----", "");

                    // 그 후 Base64 인코딩된 데이터만 유지 (공백 및 특수 문자 제거)
                    String cleanedKey = withoutHeaderFooter.replaceAll("[^A-Za-z0-9+/=]", "");
                    return cleanedKey;
                })
                .map(str -> {
                    try {
                        // 3️⃣ Base64 디코딩 시도
                        byte[] decodedBytes = Base64.getDecoder().decode(str);
                        return decodedBytes;
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid key format."));
    }


    private PrivateKey buildPrivateKey(String privateKeyStr) throws Exception {
        byte[] decoded = extractKeyBytes(privateKeyStr, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        // 바이너리 데이터에서 개인 키 객체 생성
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        // RSA 개인 키를 Java의 PrivateKey 객체로 변환.
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey buildPublicKey(String publicKeyStr) throws Exception {
        byte[] decoded = extractKeyBytes(publicKeyStr, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
