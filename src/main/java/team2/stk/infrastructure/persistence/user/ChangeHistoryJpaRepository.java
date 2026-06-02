package team2.stk.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.stk.domain.user.ChangeHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChangeHistoryJpaRepository extends JpaRepository<ChangeHistory, UUID>,
        JpaSpecificationExecutor<ChangeHistory> {

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