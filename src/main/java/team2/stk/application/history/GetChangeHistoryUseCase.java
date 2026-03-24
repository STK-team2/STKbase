package team2.stk.application.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.infrastructure.persistence.user.ChangeHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetChangeHistoryUseCase {

    private final ChangeHistoryRepository changeHistoryRepository;

    public List<ChangeHistoryDto> execute(SearchCriteria criteria) {
        log.info("변경 이력 조회 시작 - 테이블명: {}, 기간: {} ~ {}, 검색어: {}",
                criteria.tableName(), criteria.startDate(), criteria.endDate(), criteria.query());

        List<ChangeHistory> histories = changeHistoryRepository.searchChangeHistory(
                criteria.tableName(),
                criteria.startDate(),
                criteria.endDate(),
                criteria.query()
        );

        log.info("변경 이력 조회 완료 - {}건", histories.size());

        return histories.stream()
                .map(ChangeHistoryDto::from)
                .toList();
    }

    public record SearchCriteria(
            String tableName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String query
    ) {}

    public record ChangeHistoryDto(
            String id,
            String userName,
            String tableName,
            String recordId,
            String action,
            String beforeValue,
            String afterValue,
            LocalDateTime changedAt
    ) {
        public static ChangeHistoryDto from(ChangeHistory history) {
            return new ChangeHistoryDto(
                    history.getId().toString(),
                    history.getUser().getName(),
                    history.getTableName(),
                    history.getRecordId().toString(),
                    history.getAction(),
                    history.getBeforeValue(),
                    history.getAfterValue(),
                    history.getChangedAt()
            );
        }
    }
}