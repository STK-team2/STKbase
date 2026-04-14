package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetMonthlyTrendUseCase.MonthlyResult;

@Getter
@AllArgsConstructor
public class MonthlyTrendResponse {
    private final String month;
    private final long inboundTotal;
    private final long outboundTotal;

    public static MonthlyTrendResponse from(MonthlyResult result) {
        return new MonthlyTrendResponse(result.month(), result.inboundTotal(), result.outboundTotal());
    }
}
