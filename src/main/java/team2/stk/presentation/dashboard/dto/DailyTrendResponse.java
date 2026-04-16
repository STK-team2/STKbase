package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetDailyTrendUseCase.DailyResult;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyTrendResponse {
    private final LocalDate date;
    private final long inboundTotal;
    private final long outboundTotal;

    public static DailyTrendResponse from(DailyResult result) {
        return new DailyTrendResponse(result.date(), result.inboundTotal(), result.outboundTotal());
    }
}
