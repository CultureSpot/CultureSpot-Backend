package com.culturespot.culturespotdomain.auth.controller;

import com.culturespot.culturespotdomain.auth.dto.local.UserResponseDto;
import com.culturespot.culturespotdomain.auth.security.jwt.JwtUtil;
import com.culturespot.culturespotdomain.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {

  private final JwtUtil jwtUtil;
  private final UserService userService;

  @GetMapping("/success")
  public ResponseEntity<UserResponseDto> oauth2LoginSuccess(OAuth2AuthenticationToken authentication) {
    OAuth2User user = authentication.getPrincipal();
    String registrationId = authentication.getAuthorizedClientRegistrationId(); // google, kakao

    String email = extractEmail(user, registrationId);
    String username = user.getAttribute("name");
    String provider = registrationId.toUpperCase();

    UserResponseDto userResponse = userService.oauthSignIn(email, username, provider);
    String token = jwtUtil.createToken(userResponse.email(), userResponse.role(), provider, 60 * 60 * 1000);

    return ResponseEntity.ok(new UserResponseDto(userResponse.email(), userResponse.username(), userResponse.role(), token));
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
