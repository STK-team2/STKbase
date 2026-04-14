package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetWeeklyMovementsUseCase.DailyResult;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WeeklyMovementResponse {
    private final LocalDate date;
    private final long inboundCount;
    private final long outboundCount;

    public static WeeklyMovementResponse from(DailyResult result) {
        return new WeeklyMovementResponse(result.date(), result.inboundCount(), result.outboundCount());
    }
}
