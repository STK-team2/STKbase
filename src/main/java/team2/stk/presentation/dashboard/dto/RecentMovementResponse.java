package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetRecentMovementsUseCase.RecentMovementResult;
import team2.stk.domain.movement.MovementType;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecentMovementResponse {
    private final UUID movementId;
    private final String itemCode;
    private final String itemName;
    private final MovementType type;
    private final int quantity;
    private final LocalDate movementDate;
    private final String site;

    public static RecentMovementResponse from(RecentMovementResult result) {
        return new RecentMovementResponse(
                result.movementId(),
                result.itemCode(),
                result.itemName(),
                result.type(),
                result.quantity(),
                result.movementDate(),
                result.site()
        );
    }
}
