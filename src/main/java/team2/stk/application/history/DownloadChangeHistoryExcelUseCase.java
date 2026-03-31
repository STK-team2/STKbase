package team2.stk.application.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.infrastructure.excel.ExcelExporter;
import team2.stk.infrastructure.persistence.user.ChangeHistoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadChangeHistoryExcelUseCase {

    private final ChangeHistoryRepository changeHistoryRepository;
    private final ExcelExporter excelExporter;

    private static final List<String> HEADERS = List.of(
            "변경일시", "사용자", "테이블명", "레코드ID", "액션", "변경 전", "변경 후"
    );

    public ExcelResult execute(SearchCriteria criteria) {
        log.info("변경 이력 엑셀 다운로드 시작 - 테이블명: {}, 기간: {} ~ {}, 검색어: {}",
                criteria.tableName(), criteria.startDate(), criteria.endDate(), criteria.query());

        List<ChangeHistory> histories = changeHistoryRepository.searchChangeHistory(
                criteria.tableName(),
                criteria.startDate(),
                criteria.endDate(),
                criteria.query()
        );

        List<Function<ChangeHistory, Object>> columns = List.of(
                history -> history.getChangedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                history -> history.getUser().getName(),
                ChangeHistory::getTableName,
                history -> history.getRecordId().toString(),
                ChangeHistory::getAction,
                history -> formatJsonValue(history.getBeforeValue()),
                history -> formatJsonValue(history.getAfterValue())
        );

        ByteArrayResource resource = excelExporter.export(HEADERS, histories, columns);

        String filename = generateFilename(criteria);

        log.info("변경 이력 엑셀 다운로드 완료 - {}건, 파일명: {}", histories.size(), filename);

        return new ExcelResult(resource, filename);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String formatJsonValue(Map<String, Object> value) {
        if (value == null) return "";
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return value.toString();
        }
    }

    private String generateFilename(SearchCriteria criteria) {
        String dateRange = "";
        if (criteria.startDate() != null && criteria.endDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            dateRange = "_" + criteria.startDate().format(formatter) +
                       "-" + criteria.endDate().format(formatter);
        } else if (criteria.startDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            dateRange = "_" + criteria.startDate().format(formatter) + "이후";
        } else if (criteria.endDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            dateRange = "_" + criteria.endDate().format(formatter) + "이전";
        }

        String tableFilter = "";
        if (criteria.tableName() != null && !criteria.tableName().isBlank()) {
            tableFilter = "_" + criteria.tableName();
        }

        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return "변경이력" + dateRange + tableFilter + "_" + currentDate + ".xlsx";
    }

    public record SearchCriteria(
            String tableName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String query
    ) {}

    public record ExcelResult(
            ByteArrayResource resource,
            String filename
    ) {}
}