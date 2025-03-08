package com.culturespot.culturespotdomain.core.security.jwt.manager;

import com.culturespot.culturespotdomain.core.security.jwt.provider.JwtTokenProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenManager {
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> refreshTokenStore = new HashMap<>();

    public JwtTokenManager(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String issueAccessToken(String username, List<String> roles) {
        return jwtTokenProvider.createAccessToken(username, roles);
    }

    public String issueRefreshToken(String username) {
        String refreshToken = jwtTokenProvider.createRefreshToken(username);
        refreshTokenStore.put(username, refreshToken);
        return refreshToken;
    }

    public boolean verifyRefreshToken(String username, String refreshToken) {
        return refreshToken.equals(refreshTokenStore.get(username));
    }

    public void invalidateRefreshToken(String username) {
        refreshTokenStore.remove(username);
    }
}

