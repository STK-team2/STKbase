package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.movement.exception.MovementNotFoundException;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateMovementUseCase {

    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public StockMovement execute(UUID movementId, String site, int quantity, LocalDate movementDate, String reference, String note) {
        StockMovement movement = stockMovementRepository.findByIdActive(movementId)
                .orElseThrow(() -> new MovementNotFoundException(movementId.toString()));

        movement.update(site, quantity, movementDate, reference, note);

        return stockMovementRepository.save(movement);
    }
}