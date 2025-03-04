package com.culturespot.culturespotdomain.auth.dto.local;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignInRequestDto(

    @NotBlank(message = "이메일 주소를 입력해주세요")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d가-힣\\s])[A-Za-z\\d[^A-Za-z\\d가-힣\\s]]{8,16}$\n",
        message = "비밀번호는 8~16자이며, 숫자 최소 1개, 영어 최소 1개, 공백을 제외한 특수문자 최소 1개를 포함해야 합니다.")
    String password
) {}
