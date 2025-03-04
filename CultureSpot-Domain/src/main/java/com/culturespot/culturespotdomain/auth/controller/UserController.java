package com.culturespot.culturespotdomain.auth.controller;

import com.culturespot.culturespotdomain.auth.dto.local.SignInRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.UserResponseDto;
import com.culturespot.culturespotdomain.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/signup")
//  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto request) {
    SignUpRequestDto signUpDto = new SignUpRequestDto(
        request.email(),
        passwordEncoder.encode(request.password()),
        request.username());

    userService.signUp(signUpDto);
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }

  @PostMapping("/signin")
  public ResponseEntity<UserResponseDto> signIn(@Valid @RequestBody SignInRequestDto request) {
    SignInRequestDto SignInDto = new SignInRequestDto(
        request.email(),
        passwordEncoder.encode(request.password()));
    UserResponseDto response = userService.signIn(SignInDto);
    return ResponseEntity.ok(response);
  }
}
