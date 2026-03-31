package team2.stk.infrastructure.persistence.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockMovementRepository {

    private final StockMovementJpaRepository stockMovementJpaRepository;

    public Optional<StockMovement> findByIdActive(UUID id) {
        return stockMovementJpaRepository.findByIdActive(id);
    }

    public List<StockMovement> findByItemIdActive(UUID itemId) {
        return stockMovementJpaRepository.findByItemIdActive(itemId);
    }

    public List<StockMovement> findByItemIdAndTypeActive(UUID itemId, MovementType type) {
        return stockMovementJpaRepository.findByItemIdAndTypeActive(itemId, type);
    }

    public List<StockMovement> findByItemIdAndDateRangeActive(UUID itemId, LocalDate startDate, LocalDate endDate) {
        return stockMovementJpaRepository.findByItemIdAndDateRangeActive(itemId, startDate, endDate);
    }

    public List<StockMovement> searchMovements(MovementType type, LocalDate startDate, LocalDate endDate, String query) {
        return stockMovementJpaRepository.searchMovements(type, startDate, endDate, query);
    }

    public int calculateCurrentStock(UUID itemId) {
        return stockMovementJpaRepository.calculateCurrentStock(itemId);
    }

    public StockMovementJpaRepository.MovementSummary getMovementSummary(UUID itemId, LocalDate startDate, LocalDate endDate) {
        return stockMovementJpaRepository.getMovementSummary(itemId, startDate, endDate);
    }

    public List<StockMovementJpaRepository.ItemStockProjection> calculateAllCurrentStock() {
        return stockMovementJpaRepository.calculateAllCurrentStock();
    }

    public StockMovement save(StockMovement stockMovement) {
        return stockMovementJpaRepository.save(stockMovement);
    }
}