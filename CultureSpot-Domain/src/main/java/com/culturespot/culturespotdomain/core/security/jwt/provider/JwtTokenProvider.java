package com.culturespot.culturespotdomain.core.security.jwt.provider;

import com.culturespot.culturespotdomain.core.security.jwt.generator.JwtTokenGenerator;
import com.culturespot.culturespotdomain.core.security.jwt.validator.JwtTokenValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenProvider {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenValidator jwtTokenValidator;

    public JwtTokenProvider(JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    public String createAccessToken(String username, List<String> roles) {
        return jwtTokenGenerator.generateAccessToken(username, roles);
    }

    public String createRefreshToken(String username) {
        return jwtTokenGenerator.generateRefreshToken(username);
    }

    public boolean isValidToken(String token) {
        return jwtTokenValidator.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtTokenValidator.getUsernameFromToken(token);
    }
}
