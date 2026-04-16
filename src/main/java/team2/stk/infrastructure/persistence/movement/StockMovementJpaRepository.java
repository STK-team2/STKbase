package team2.stk.infrastructure.persistence.movement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementJpaRepository extends JpaRepository<StockMovement, UUID> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.id = :id AND sm.deletedAt IS NULL")
    Optional<StockMovement> findByIdActive(@Param("id") UUID id);

    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.item.id = :itemId AND sm.deletedAt IS NULL " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    List<StockMovement> findByItemIdActive(@Param("itemId") UUID itemId);

    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.item.id = :itemId AND sm.type = :type AND sm.deletedAt IS NULL " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    List<StockMovement> findByItemIdAndTypeActive(@Param("itemId") UUID itemId, @Param("type") MovementType type);

    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.item.id = :itemId AND sm.deletedAt IS NULL " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    List<StockMovement> findByItemIdAndDateRangeActive(
            @Param("itemId") UUID itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT sm FROM StockMovement sm " +
           "JOIN FETCH sm.item i " +
           "WHERE sm.deletedAt IS NULL " +
           "AND (:type IS NULL OR sm.type = :type) " +
           "AND (:startDate IS NULL OR sm.movementDate >= :startDate) " +
           "AND (:endDate IS NULL OR sm.movementDate <= :endDate) " +
           "AND (:query IS NULL OR :query = '' OR " +
           "     LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "     LOWER(i.itemName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    List<StockMovement> searchMovements(
            @Param("type") MovementType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("query") String query
    );

    // 현재 재고 계산을 위한 집계 쿼리
    // 재고 증가: INBOUND, RETURN_OUTBOUND, EXCHANGE_IN
    // 재고 감소: OUTBOUND, RETURN_INBOUND, EXCHANGE_OUT
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN sm.quantity ELSE 0 END), 0) - " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN sm.quantity ELSE 0 END), 0) " +
           "FROM StockMovement sm " +
           "WHERE sm.item.id = :itemId AND sm.deletedAt IS NULL")
    int calculateCurrentStock(@Param("itemId") UUID itemId);

    // 기간별 재고 변동량 집계
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN sm.quantity ELSE 0 END), 0) as inboundQty, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN sm.quantity ELSE 0 END), 0) as outboundQty " +
           "FROM StockMovement sm " +
           "WHERE sm.item.id = :itemId AND sm.deletedAt IS NULL " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate")
    MovementSummary getMovementSummary(
            @Param("itemId") UUID itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 모든 활성 아이템의 현재 재고를 한 번에 계산 (N+1 방지)
    @Query("SELECT sm.item.id as itemId, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN sm.quantity ELSE 0 END), 0) - " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN sm.quantity ELSE 0 END), 0) as stock " +
           "FROM StockMovement sm " +
           "WHERE sm.deletedAt IS NULL " +
           "GROUP BY sm.item.id")
    List<ItemStockProjection> calculateAllCurrentStock();

    interface ItemStockProjection {
        UUID getItemId();
        int getStock();
    }

    interface MovementSummary {
        int getInboundQty();
        int getOutboundQty();
    }

    // 오늘 입고 건수
    @Query("SELECT COUNT(sm) FROM StockMovement sm " +
           "WHERE sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') " +
           "AND sm.movementDate = :date AND sm.deletedAt IS NULL")
    long countTodayInbound(@Param("date") LocalDate date);

    // 오늘 출고 건수
    @Query("SELECT COUNT(sm) FROM StockMovement sm " +
           "WHERE sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') " +
           "AND sm.movementDate = :date AND sm.deletedAt IS NULL")
    long countTodayOutbound(@Param("date") LocalDate date);

    // 일별 입고/출고 건수 (주간 현황용)
    @Query("SELECT sm.movementDate as date, " +
           "SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN 1 ELSE 0 END) as inboundCount, " +
           "SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN 1 ELSE 0 END) as outboundCount " +
           "FROM StockMovement sm " +
           "WHERE sm.movementDate BETWEEN :startDate AND :endDate AND sm.deletedAt IS NULL " +
           "GROUP BY sm.movementDate " +
           "ORDER BY sm.movementDate")
    List<DailyMovementCount> countDailyMovements(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    interface DailyMovementCount {
        LocalDate getDate();
        long getInboundCount();
        long getOutboundCount();
    }

    // 최근 입출고 N건
    @Query("SELECT sm FROM StockMovement sm " +
           "JOIN FETCH sm.item i " +
           "WHERE sm.deletedAt IS NULL " +
           "ORDER BY sm.createdAt DESC " +
           "LIMIT :limit")
    List<StockMovement> findRecentMovements(@Param("limit") int limit);

    // 월별 입고/출고 합계 (월별 추이용)
    @Query("SELECT FUNCTION('TO_CHAR', sm.movementDate, 'YYYY-MM') as month, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN sm.quantity ELSE 0 END), 0) as inboundTotal, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN sm.quantity ELSE 0 END), 0) as outboundTotal " +
           "FROM StockMovement sm " +
           "WHERE sm.movementDate BETWEEN :startDate AND :endDate AND sm.deletedAt IS NULL " +
           "GROUP BY FUNCTION('TO_CHAR', sm.movementDate, 'YYYY-MM') " +
           "ORDER BY FUNCTION('TO_CHAR', sm.movementDate, 'YYYY-MM')")
    List<MonthlyMovementTotal> getMonthlyTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    interface MonthlyMovementTotal {
        String getMonth();
        long getInboundTotal();
        long getOutboundTotal();
    }

    // 일별 입고/출고 수량 합계 (일별 추이용)
    @Query("SELECT sm.movementDate as date, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('INBOUND', 'RETURN_OUTBOUND', 'EXCHANGE_IN') THEN sm.quantity ELSE 0 END), 0) as inboundTotal, " +
           "COALESCE(SUM(CASE WHEN sm.type IN ('OUTBOUND', 'RETURN_INBOUND', 'EXCHANGE_OUT') THEN sm.quantity ELSE 0 END), 0) as outboundTotal " +
           "FROM StockMovement sm " +
           "WHERE sm.movementDate BETWEEN :startDate AND :endDate AND sm.deletedAt IS NULL " +
           "GROUP BY sm.movementDate " +
           "ORDER BY sm.movementDate")
    List<DailyMovementTotal> getDailyTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    interface DailyMovementTotal {
        LocalDate getDate();
        long getInboundTotal();
        long getOutboundTotal();
    }
}