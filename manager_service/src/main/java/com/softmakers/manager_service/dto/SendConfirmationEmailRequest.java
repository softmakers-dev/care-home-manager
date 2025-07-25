package com.softmakers.manager_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.*;

import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SendConfirmationEmailRequest {

    @NotBlank(message = "username을 입력해주세요")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
    private String userName;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일의 형식이 맞지 않습니다")
    private String email;
}
