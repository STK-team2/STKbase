package team2.stk.presentation.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyEmailRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@stk-eng\\.com$", message = "사내 이메일만 사용할 수 있습니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Pattern(regexp = "\\d{6}", message = "인증 코드는 6자리 숫자여야 합니다.")
    private String code;
}
