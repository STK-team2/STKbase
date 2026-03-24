package team2.stk.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.stk.domain.user.ChangeHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChangeHistoryJpaRepository extends JpaRepository<ChangeHistory, UUID> {

    @Query("SELECT ch FROM ChangeHistory ch " +
           "JOIN FETCH ch.user u " +
           "WHERE (:tableName IS NULL OR ch.tableName = :tableName) " +
           "AND (:startDate IS NULL OR ch.changedAt >= :startDate) " +
           "AND (:endDate IS NULL OR ch.changedAt <= :endDate) " +
           "AND (:query IS NULL OR :query = '' OR " +
           "     LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "     LOWER(ch.tableName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "     LOWER(ch.action) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY ch.changedAt DESC")
    List<ChangeHistory> searchChangeHistory(
            @Param("tableName") String tableName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("query") String query
    );

    @Query("SELECT ch FROM ChangeHistory ch " +
           "WHERE ch.recordId = :recordId AND ch.tableName = :tableName " +
           "ORDER BY ch.changedAt DESC")
    List<ChangeHistory> findByRecordIdAndTableName(
            @Param("recordId") UUID recordId,
            @Param("tableName") String tableName
    );

    @Query("SELECT ch FROM ChangeHistory ch " +
           "WHERE ch.user.id = :userId " +
           "ORDER BY ch.changedAt DESC")
    List<ChangeHistory> findByUserId(@Param("userId") UUID userId);
}