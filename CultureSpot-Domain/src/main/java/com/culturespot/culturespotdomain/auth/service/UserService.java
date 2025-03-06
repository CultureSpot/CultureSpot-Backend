package com.culturespot.culturespotdomain.auth.service;

import com.culturespot.culturespotdomain.auth.dto.local.SignInRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.SignUpRequestDto;
import com.culturespot.culturespotdomain.auth.dto.local.UserResponseDto;

public interface UserService {
  void signUp(SignUpRequestDto request);
  UserResponseDto signIn(SignInRequestDto request);
}
