package com.ohyes.GrowUpMoney.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, message = "아이디는 4자리 이상이어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자리 이상이어야 합니다."
    )
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

}
