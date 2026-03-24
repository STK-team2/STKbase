package team2.stk.presentation.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team2.stk.application.history.DownloadChangeHistoryExcelUseCase;
import team2.stk.application.history.GetChangeHistoryUseCase;
import team2.stk.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "변경 이력", description = "변경 이력 조회 API")
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class HistoryController {

    private final GetChangeHistoryUseCase getChangeHistoryUseCase;
    private final DownloadChangeHistoryExcelUseCase downloadChangeHistoryExcelUseCase;

    @Operation(
        summary = "변경 이력 조회",
        description = """
            시스템 내 모든 변경 이력을 조회합니다.

            **조회 조건:**
            - tableName: 테이블명 필터 (stock_movement, items 등)
            - startDate: 시작 일시 (YYYY-MM-DDTHH:mm:ss)
            - endDate: 종료 일시 (YYYY-MM-DDTHH:mm:ss)
            - query: 검색어 (사용자명, 테이블명, 액션에서 검색)

            **응답:**
            - 변경 일시 내림차순으로 정렬
            - 변경 전후 값을 JSON 형태로 제공
            """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<GetChangeHistoryUseCase.ChangeHistoryDto>>> getChangeHistory(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String query
    ) {
        GetChangeHistoryUseCase.SearchCriteria criteria = new GetChangeHistoryUseCase.SearchCriteria(
                tableName, startDate, endDate, query
        );

        List<GetChangeHistoryUseCase.ChangeHistoryDto> histories = getChangeHistoryUseCase.execute(criteria);

        return ResponseEntity.ok(ApiResponse.success(histories));
    }

    @Operation(
        summary = "변경 이력 엑셀 다운로드",
        description = """
            변경 이력을 엑셀 파일로 다운로드합니다.

            **파일명 규칙:**
            - 변경이력_YYYYMMDD-YYYYMMDD_테이블명_생성일.xlsx
            - 예: 변경이력_20250101-20250131_stock_movement_20250201.xlsx

            **파일 내용:**
            - 변경일시, 사용자, 테이블명, 레코드ID, 액션, 변경 전, 변경 후
            - JSON 값은 가독성을 위해 포맷팅됨
            """
    )
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadChangeHistory(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String query
    ) {
        try {
            DownloadChangeHistoryExcelUseCase.SearchCriteria criteria =
                    new DownloadChangeHistoryExcelUseCase.SearchCriteria(tableName, startDate, endDate, query);

            DownloadChangeHistoryExcelUseCase.ExcelResult result = downloadChangeHistoryExcelUseCase.execute(criteria);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(result.resource());

        } catch (Exception e) {
            log.error("변경 이력 엑셀 다운로드 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}