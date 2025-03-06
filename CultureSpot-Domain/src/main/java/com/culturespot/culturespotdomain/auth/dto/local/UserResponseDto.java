package com.culturespot.culturespotdomain.auth.dto.local;

public record UserResponseDto (
  String email,
  String username,
  String role,
  String token
) {}