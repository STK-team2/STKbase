package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRecentMovementsUseCase {

    private final StockMovementRepository stockMovementRepository;

    public List<RecentMovementResult> execute(int limit) {
        List<StockMovement> movements = stockMovementRepository.findRecentMovements(limit);

        return movements.stream()
                .map(sm -> new RecentMovementResult(
                        sm.getId(),
                        sm.getItem().getItemCode(),
                        sm.getItem().getItemName(),
                        sm.getType(),
                        sm.getQuantity(),
                        sm.getMovementDate(),
                        sm.getSite()
                ))
                .toList();
    }

    public record RecentMovementResult(
            UUID movementId,
            String itemCode,
            String itemName,
            MovementType type,
            int quantity,
            LocalDate movementDate,
            String site
    ) {}
}
