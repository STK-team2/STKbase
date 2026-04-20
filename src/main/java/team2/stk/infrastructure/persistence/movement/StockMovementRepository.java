package team2.stk.infrastructure.persistence.movement;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;

import java.time.LocalDate;
import java.util.ArrayList;
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
        Specification<StockMovement> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Join<StockMovement, Item> itemJoin = root.join("item", JoinType.INNER);
            criteriaQuery.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("movementDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("movementDate"), endDate));
            }

            if (query != null && !query.isBlank()) {
                String normalizedQuery = "%" + query.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(itemJoin.get("itemCode")), normalizedQuery),
                        criteriaBuilder.like(criteriaBuilder.lower(itemJoin.get("itemName")), normalizedQuery)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        Sort sort = Sort.by(
                Sort.Order.desc("movementDate"),
                Sort.Order.desc("createdAt")
        );

        return stockMovementJpaRepository.findAll(specification, sort);
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

    public long countTodayInbound(LocalDate date) {
        return stockMovementJpaRepository.countTodayInbound(date);
    }

    public long countTodayOutbound(LocalDate date) {
        return stockMovementJpaRepository.countTodayOutbound(date);
    }

    public List<StockMovementJpaRepository.DailyMovementCount> countDailyMovements(LocalDate startDate, LocalDate endDate) {
        return stockMovementJpaRepository.countDailyMovements(startDate, endDate);
    }

    public List<StockMovement> findRecentMovements(int limit) {
        return stockMovementJpaRepository.findRecentMovements(limit);
    }

    public List<StockMovementJpaRepository.MonthlyMovementTotal> getMonthlyTrend(LocalDate startDate, LocalDate endDate) {
        return stockMovementJpaRepository.getMonthlyTrend(startDate, endDate);
    }

    public List<StockMovementJpaRepository.DailyMovementTotal> getDailyTrend(LocalDate startDate, LocalDate endDate) {
        return stockMovementJpaRepository.getDailyTrend(startDate, endDate);
    }

    public StockMovement save(StockMovement stockMovement) {
        return stockMovementJpaRepository.save(stockMovement);
    }
}
