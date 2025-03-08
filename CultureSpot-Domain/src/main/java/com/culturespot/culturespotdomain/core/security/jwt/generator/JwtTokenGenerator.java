package com.culturespot.culturespotdomain.core.security.jwt.generator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenGenerator {
    private final PrivateKey privateKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenGenerator(
            PrivateKey privateKey,
            @Value("${spring.jwt.access-expiration-time}") long accessExpirationTime,
            @Value("${spring.jwt.refresh-expiration-time}") long refreshExpirationTime
    ) {
        this.privateKey = privateKey;
        this.accessExpirationTime = accessExpirationTime * 1000; // 초 단위를 밀리초로 변환
        this.refreshExpirationTime = refreshExpirationTime * 1000;
    }

    public String generateAccessToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)  // 역할 정보 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
