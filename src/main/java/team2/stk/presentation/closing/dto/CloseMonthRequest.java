package team2.stk.presentation.closing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CloseMonthRequest {

    @NotBlank(message = "마감 연월은 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "마감 연월은 YYYY-MM 형식이어야 합니다.")
    private String closingYm;

    /** 단건 마감 시 자재 ID (null이면 전체 마감) */
    private UUID itemId;
}