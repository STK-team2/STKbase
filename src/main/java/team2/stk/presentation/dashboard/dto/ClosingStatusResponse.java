package team2.stk.presentation.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.dashboard.GetClosingStatusUseCase.ClosingStatusResult;

@Getter
@AllArgsConstructor
public class ClosingStatusResponse {
    private final String closingYm;
    private final boolean isClosed;
    private final long closedCount;
    private final long unclosedCount;
    private final long totalClosedAll;

    public static ClosingStatusResponse from(ClosingStatusResult result) {
        return new ClosingStatusResponse(
                result.closingYm(),
                result.isClosed(),
                result.closedCount(),
                result.unclosedCount(),
                result.totalClosedAll()
        );
    }
}
