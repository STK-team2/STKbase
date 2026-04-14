package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    private static final int DEFAULT_LIMIT = 5;

    private final StockMovementRepository stockMovementRepository;

    public List<RecentMovementResult> execute() {
        List<StockMovement> movements = stockMovementRepository.findRecentMovements(PageRequest.of(0, DEFAULT_LIMIT));

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
