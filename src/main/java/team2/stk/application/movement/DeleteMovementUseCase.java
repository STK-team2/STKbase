package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.movement.exception.MovementNotFoundException;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteMovementUseCase {

    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public void execute(UUID movementId) {
        StockMovement movement = stockMovementRepository.findByIdActive(movementId)
                .orElseThrow(() -> new MovementNotFoundException(movementId.toString()));

        movement.delete();
        stockMovementRepository.save(movement);
    }
}