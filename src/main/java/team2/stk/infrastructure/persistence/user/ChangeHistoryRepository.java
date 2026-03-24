package team2.stk.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.user.ChangeHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChangeHistoryRepository {

    private final ChangeHistoryJpaRepository changeHistoryJpaRepository;

    public ChangeHistory save(ChangeHistory changeHistory) {
        return changeHistoryJpaRepository.save(changeHistory);
    }

    public List<ChangeHistory> searchChangeHistory(String tableName, LocalDateTime startDate,
                                                  LocalDateTime endDate, String query) {
        return changeHistoryJpaRepository.searchChangeHistory(tableName, startDate, endDate, query);
    }

    public List<ChangeHistory> findByRecordIdAndTableName(UUID recordId, String tableName) {
        return changeHistoryJpaRepository.findByRecordIdAndTableName(recordId, tableName);
    }

    public List<ChangeHistory> findByUserId(UUID userId) {
        return changeHistoryJpaRepository.findByUserId(userId);
    }
}