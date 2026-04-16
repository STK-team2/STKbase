package team2.stk.presentation.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team2.stk.application.dashboard.*;
import team2.stk.presentation.dashboard.dto.*;
import team2.stk.shared.response.ApiResponse;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "대시보드", description = "대시보드 API")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;
    private final GetClosingStatusUseCase getClosingStatusUseCase;
    private final GetWeeklyMovementsUseCase getWeeklyMovementsUseCase;
    private final GetRecentMovementsUseCase getRecentMovementsUseCase;
    private final GetMonthlyTrendUseCase getMonthlyTrendUseCase;
    private final GetDailyTrendUseCase getDailyTrendUseCase;

    @Operation(summary = "대시보드 요약", description = "오늘 입고/출고 건수, 전체 품목 수를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        DashboardSummaryResponse response = DashboardSummaryResponse.from(getDashboardSummaryUseCase.execute());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "이번 달 마감 현황", description = "이번 달 마감 여부, 마감/미마감 건수, 전체 마감 건수를 조회합니다.")
    @GetMapping("/closing-status")
    public ResponseEntity<ApiResponse<ClosingStatusResponse>> getClosingStatus() {
        ClosingStatusResponse response = ClosingStatusResponse.from(getClosingStatusUseCase.execute());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "주간 입출고 현황", description = "이번 주 월~금 일별 입고/출고 건수를 조회합니다.")
    @GetMapping("/weekly-movements")
    public ResponseEntity<ApiResponse<List<WeeklyMovementResponse>>> getWeeklyMovements() {
        List<WeeklyMovementResponse> responses = getWeeklyMovementsUseCase.execute().stream()
                .map(WeeklyMovementResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "최근 입출고", description = "최근 입출고 내역을 조회합니다. limit 파라미터로 조회 건수를 지정할 수 있습니다.")
    @GetMapping("/recent-movements")
    public ResponseEntity<ApiResponse<List<RecentMovementResponse>>> getRecentMovements(
            @RequestParam(defaultValue = "5") int limit) {
        List<RecentMovementResponse> responses = getRecentMovementsUseCase.execute(limit).stream()
                .map(RecentMovementResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "월별 재고 추이", description = "최근 12개월 월별 입고/출고 합계를 조회합니다.")
    @GetMapping("/monthly-trend")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getMonthlyTrend() {
        List<MonthlyTrendResponse> responses = getMonthlyTrendUseCase.execute().stream()
                .map(MonthlyTrendResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "일별 재고 추이", description = "기간별 일별 입고/출고 수량 합계를 조회합니다. 기간 미지정 시 최근 30일입니다.")
    @GetMapping("/daily-trend")
    public ResponseEntity<ApiResponse<List<DailyTrendResponse>>> getDailyTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DailyTrendResponse> responses = getDailyTrendUseCase.execute(from, to).stream()
                .map(DailyTrendResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
