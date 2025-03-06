package com.culturespot.culturespotdomain.auth.service;

import com.culturespot.culturespotdomain.auth.dto.local.SignInRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.UserResponseDto;
import com.culturespot.culturespotdomain.auth.entity.User;
import com.culturespot.culturespotdomain.auth.enums.OAuthProvider;
import com.culturespot.culturespotdomain.auth.enums.UserRole;
import com.culturespot.culturespotdomain.auth.repository.UserRepository;
import com.culturespot.culturespotdomain.auth.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public void signUp(SignUpRequestDto request) {

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }

    if (userRepository.findByUsername(request.username()).isPresent()) {
      throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
    }

    User user = User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .username(request.username())
        .oAuthProvider(OAuthProvider.LOCAL)
        .userRoles(UserRole.USER)
        .build();

    userRepository.save(user);
  }

  @Override
  public UserResponseDto signIn(SignInRequestDto request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 주소 또는 비밀번호입니다."));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new IllegalArgumentException("잘못된 이메일 주소 또는 비밀번호입니다.");
    }

    String token = jwtUtil.createToken(user.getEmail(), user.getUserRoles().name(), "LOCAL", 60 * 60 * 1000);

    return new UserResponseDto(user.getEmail(), user.getUsername(), user.getUserRoles().name(), token);
  }

  @Override
  public UserResponseDto oauthSignIn(String email, String username, String provider) {
    Optional<User> existingUser = userRepository.findByEmail(email);

    User user;
    if (existingUser.isPresent()) {
      user = existingUser.get();
    } else {
      user = User.builder()
          .email(email)
          .password(null)
          .username(username)
          .oAuthProvider(OAuthProvider.valueOf(provider.toUpperCase()))
          .userRoles(UserRole.USER)
          .build();

      userRepository.save(user);
    }

    String token = jwtUtil.createToken(user.getEmail(), user.getUserRoles().name(), provider, 60 * 60 * 1000);

    return new UserResponseDto(user.getEmail(), user.getUsername(), user.getUserRoles().name(), token);
  }
}
