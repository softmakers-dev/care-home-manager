package com.softmakers.manager_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "username을 입력해주세요")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
    private String userName;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일의 형식이 맞지 않습니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
    private String password;

    @NotBlank(message = "이메일 인증코드를 입력해주세요")
    @Length(max = 6, min = 6, message = "인증코드는 6자리 입니다.")
    private String code;
}
