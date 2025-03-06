package com.culturespot.culturespotdomain.auth.dto.local;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequestDto (

    @NotBlank(message = "이메일 주소를 입력해주세요")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
        regexp =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d가-힣\\s])[A-Za-z\\d[^A-Za-z\\d가-힣\\s]]{8,16}$\n",
        message = "비밀번호는 8~16자이며, 숫자 최소 1개, 영어 최소 1개, 공백을 제외한 특수문자 최소 1개를 포함해야 합니다.")
    String password,

    @NotBlank(message = "닉네임을 입력해주세요")
    @Pattern(
        regexp = "^[가-힣a-zA-Z0-9]{2,8}$",
        message = "닉네임은 2~8자의 한글, 영어, 숫자만 사용할 수 있으며, 공백 및 특수문자는 포함할 수 없습니다.")
    String username
) {}