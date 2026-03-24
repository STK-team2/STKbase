package team2.stk.presentation.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterItemRequest {

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
}