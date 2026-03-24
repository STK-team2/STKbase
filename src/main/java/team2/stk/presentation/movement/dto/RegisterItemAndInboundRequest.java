package team2.stk.presentation.movement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegisterItemAndInboundRequest {

    @NotBlank(message = "자재코드는 필수입니다.")
    @Size(max = 50, message = "자재코드는 50자 이내여야 합니다.")
    private String itemCode;

    @NotBlank(message = "자재명은 필수입니다.")
    @Size(max = 100, message = "자재명은 100자 이내여야 합니다.")
    private String itemName;

    @Size(max = 50, message = "BOX번호는 50자 이내여야 합니다.")
    private String boxNumber;

    @NotBlank(message = "위치는 필수입니다.")
    @Size(max = 100, message = "위치는 100자 이내여야 합니다.")
    private String location;

    @NotBlank(message = "사업장은 필수입니다.")
    private String site;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int quantity;

    @NotNull(message = "입고 날짜는 필수입니다.")
    private LocalDate movementDate;

    private String reference;

    private String note;
}