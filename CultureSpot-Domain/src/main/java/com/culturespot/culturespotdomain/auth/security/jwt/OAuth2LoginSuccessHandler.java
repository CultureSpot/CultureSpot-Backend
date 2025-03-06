package com.culturespot.culturespotdomain.auth.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
      OAuth2User user = oauthToken.getPrincipal();
      String registrationId = oauthToken.getAuthorizedClientRegistrationId();

      String email = extractEmail(user, registrationId);
      String provider = registrationId.toUpperCase();

      String token = jwtUtil.createToken(email, "USER", provider, 60 * 60 * 1000);

      response.sendRedirect("/api/v1/oauth2/success?token=" + token);
    }
  }

  private String extractEmail(OAuth2User user, String registrationId) {
    if ("google".equals(registrationId)) {
      return user.getAttribute("email");
    } else if ("kakao".equals(registrationId)) {
      Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
      return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }
    return null;
  }
}
