package com.sign.domain.member.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class SignupRequest {

    @Pattern(regexp = "^[가-힣a-zA-Z][^!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?\\s]*$",
            message = "이름에는 특수문자를 포함할 수 없고 숫자로 시작할 수 없습니다.")
    @Size(min = 2, max = 10)
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[^\s]{8,16}$",
            message = "알파벳, 숫자, 공백을 제외한 특수문자를 모두 포함해야 합니다.")
    @Size(min = 8, max = 16)
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @Transient
    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @Email
    @NotEmpty(message = "이메일은 필수항목입니다.")
    private String email;
}
