package team2.stk.presentation.movement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MovementResponse {
    private final UUID id;
    private final UUID itemId;
    private final String itemCode;
    private final String itemName;
    private final String site;
    private final MovementType type;
    private final int quantity;
    private final LocalDate movementDate;
    private final String reference;
    private final String note;
    private final String userName;
    private final LocalDateTime createdAt;

    public static MovementResponse from(StockMovement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getItem().getId(),
                movement.getItem().getItemCode(),
                movement.getItem().getItemName(),
                movement.getSite(),
                movement.getType(),
                movement.getQuantity(),
                movement.getMovementDate(),
                movement.getReference(),
                movement.getNote(),
                movement.getUser().getName(),
                movement.getCreatedAt()
        );
    }
}