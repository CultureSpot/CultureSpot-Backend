package com.culturespot.culturespotdomain.auth.controller;

import com.culturespot.culturespotdomain.auth.dto.local.SignInRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.UserResponseDto;
import com.culturespot.culturespotdomain.auth.security.jwt.JwtUtil;
import com.culturespot.culturespotdomain.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto request) {
    userService.signUp(request);
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }

  @PostMapping("/signin")
  public ResponseEntity<UserResponseDto> login(@Valid @RequestBody SignInRequestDto request) {
    UserResponseDto userResponse = userService.signIn(request);

    String token = jwtUtil.createToken(userResponse.email(), userResponse.role(), "LOCAL", 60 * 60 * 1000);
    return ResponseEntity.ok(new UserResponseDto(userResponse.email(), userResponse.role(), token, token));
  }
}
