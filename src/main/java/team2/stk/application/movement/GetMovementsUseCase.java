package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.presentation.movement.dto.MovementResponse;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMovementsUseCase {

    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<MovementResult> execute(MovementType type, LocalDate from, LocalDate to, String query) {
        List<StockMovement> movements = stockMovementRepository.searchMovements(type, from, to, query);

        return movements.stream()
                .map(movement -> new MovementResult(
                        movement.getId(),
                        movement.getItem().getId(),
                        movement.getItem().getItemCode(),
                        movement.getItem().getItemName(),
                        movement.getItem().getLocation(),
                        movement.getSite(),
                        movement.getType(),
                        movement.getQuantity(),
                        movement.getMovementDate(),
                        movement.getReference(),
                        movement.getNote(),
                        movement.getUser().getName(),
                        movement.getCreatedAt()
                ))
                .toList();
    }

    public record MovementResult(
            java.util.UUID movementId,
            java.util.UUID itemId,
            String itemCode,
            String itemName,
            String location,
            String site,
            MovementType type,
            int quantity,
            LocalDate movementDate,
            String reference,
            String note,
            String userName,
            java.time.LocalDateTime createdAt
    ) {}
}
