package com.culturespot.culturespotdomain.User;

import com.culturespot.culturespotdomain.auth.dto.SignInRequestDto;
import com.culturespot.culturespotdomain.auth.dto.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.dto.UserResponseDto;
import com.culturespot.culturespotdomain.auth.entity.User;
import com.culturespot.culturespotdomain.auth.repository.MemoryUserRepository;
import com.culturespot.culturespotdomain.auth.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceImplTest {

  private UserServiceImpl userService;
  private MemoryUserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userRepository = new MemoryUserRepository(); // 메모리 저장소 사용
    passwordEncoder = new BCryptPasswordEncoder(); // 실제 비밀번호 암호화 사용
    userService = new UserServiceImpl(userRepository, passwordEncoder);
  }

  @Test
  void 회원가입_성공() {
    // Given: 유효한 회원가입 요청 DTO
    SignUpRequestDto requestDto = new SignUpRequestDto("test@gmail.com", "password123", "test");

    // When
    System.out.println(requestDto.email());
    userService.signUp(requestDto);

    // Then
    Optional<User> savedUser = userRepository.findByEmail(requestDto.email());
    assertThat(savedUser).isPresent();
    assertThat(savedUser.get().getEmail()).isEqualTo(requestDto.email());
    assertThat(savedUser.get().getUsername()).isEqualTo(requestDto.username());
    assertThat(passwordEncoder.matches("password123", savedUser.get().getPassword())).isTrue();
    assertThat(savedUser.get().getCreatedAt()).isNotNull();
  }

  @Test
  void 회원가입_실패_중복이메일() {
    // Given: 이미 가입된 이메일
    SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "password123", "testuser");

    userService.signUp(requestDto);
    userService.signUp(requestDto);

    // When & Then: 중복 이메일 가입 시 예외 발생
    assertThrows(IllegalArgumentException.class, () -> userService.signUp(requestDto));
  }

  @Test
  void 회원가입_실패_패스워드_길이() {
    // Given: 패스워드가 너무 짧음 (최소 8자 필요)
    SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "pa23", "testuser");

    userService.signUp(requestDto);

    // When & Then: 패스워드 길이 오류 발생
    assertThrows(RuntimeException.class, () -> userService.signUp(requestDto));
  }

  @Test
  void 회원가입_실패_닉네임_길이() {
    // Given: 닉네임이 너무 짧음 (최소 2자 필요)
    SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "password123", "t");

    // When
    userService.signUp(requestDto);
    // Then: 닉네임 길이 오류 발생
    assertThrows(IllegalArgumentException.class, () -> userService.signUp(requestDto));
  }

  @Test
  void 로그인_성공() {
    // Given
    SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "password123", "testuser");

    userService.signUp(requestDto);

    SignInRequestDto signInRequest = new SignInRequestDto("test@example.com", "password123");

    // When
    UserResponseDto response = userService.signIn(signInRequest);

    // Then
    assertThat(response.email()).isEqualTo(requestDto.email());
    assertThat(response.username()).isEqualTo(requestDto.username());
  }

  @Test
  void 로그인_실패_잘못된_이메일() {
    // Given
    SignInRequestDto signInRequest = new SignInRequestDto("wrong@example.com", "password123");

    // When
    UserResponseDto response = userService.signIn(signInRequest);
    // Then
    assertThat(response.email()).isEqualTo("wrong@example.com");
  }

  @Test
  void 로그인_실패_잘못된_비밀번호() {
    // Given
    SignUpRequestDto requestDto = new SignUpRequestDto("test@example.com", "password123", "testuser");

    userService.signUp(requestDto);

    SignInRequestDto signInRequest = new SignInRequestDto("test@example.com", "wrongpassword");

    // When
    UserResponseDto response = userService.signIn(signInRequest);
    // Then
    assertThat(response.email()).isEqualTo("wrong@example.com");
  }
}
