package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetDashboardSummaryUseCase.SummaryResult;

@Getter
@AllArgsConstructor
public class DashboardSummaryResponse {
    private final long todayInbound;
    private final long todayOutbound;
    private final long totalItems;

    public static DashboardSummaryResponse from(SummaryResult result) {
        return new DashboardSummaryResponse(result.todayInbound(), result.todayOutbound(), result.totalItems());
    }
}
