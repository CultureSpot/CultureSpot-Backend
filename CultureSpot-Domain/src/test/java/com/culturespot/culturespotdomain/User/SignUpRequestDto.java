package com.culturespot.culturespotdomain.User;

import com.culturespot.culturespotdomain.auth.controller.UserController;
import com.culturespot.culturespotdomain.auth.dto.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class SignUpRequestDtoTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void 회원가입_유효성_검사_성공() throws Exception {
    // Given: 올바른 회원가입 요청 데이터
    SignUpRequestDto requestDto = new SignUpRequestDto();
    requestDto.setEmail("test@example.com");
    requestDto.setPassword("password123");
    requestDto.setUsername("testuser");

    // When & Then: 정상적으로 회원가입 요청이 처리되어야 함
    mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(content().string("회원가입이 완료되었습니다."));
  }

  @Test
  void 회원가입_실패_이메일_누락() throws Exception {
    // Given
    SignUpRequestDto requestDto = new SignUpRequestDto();
    requestDto.setPassword("password123");
    requestDto.setUsername("testuser");

    // When & Then
    mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("이메일 주소를 입력해주세요"));
  }

  @Test
  void 회원가입_실패_이메일_형식_오류() throws Exception {
    // Given: 잘못된 이메일 형식
    SignUpRequestDto requestDto = new SignUpRequestDto();
    requestDto.setEmail("invalid-email");
    requestDto.setPassword("password123");
    requestDto.setUsername("testuser");

    // When & Then: 이메일 형식 오류 메시지가 반환되어야 함
    mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("유효한 이메일 주소를 입력해주세요"));
  }

  @Test
  void 회원가입_실패_비밀번호_길이_오류() throws Exception {
    // Given: 비밀번호 길이가 너무 짧음
    SignUpRequestDto requestDto = new SignUpRequestDto();
    requestDto.setEmail("test@example.com");
    requestDto.setPassword("short");
    requestDto.setUsername("testuser");

    // When & Then: 비밀번호 길이 오류 메시지가 반환되어야 함
    mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.password").value("비밀번호 길이가 맞지 않습니다."));
  }

  @Test
  void 회원가입_실패_닉네임_누락() throws Exception {
    // Given: 닉네임이 없는 회원가입 요청
    SignUpRequestDto requestDto = new SignUpRequestDto();
    requestDto.setEmail("test@example.com");
    requestDto.setPassword("password123");

    // When & Then: 닉네임 누락 에러 메시지가 반환되어야 함
    mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.username").value("닉네임을 입력해주세요"));
  }
}
