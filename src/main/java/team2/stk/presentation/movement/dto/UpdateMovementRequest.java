package team2.stk.presentation.movement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateMovementRequest {

    @NotBlank(message = "사업장은 필수입니다.")
    private String site;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int quantity;

    @NotNull(message = "입출고 날짜는 필수입니다.")
    private LocalDate movementDate;

    private String reference;

    private String note;
}