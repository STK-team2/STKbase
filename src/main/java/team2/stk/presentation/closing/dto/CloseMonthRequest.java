package team2.stk.presentation.closing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CloseMonthRequest {

    @NotBlank(message = "마감 연월은 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "마감 연월은 YYYY-MM 형식이어야 합니다.")
    private String closingYm;
}