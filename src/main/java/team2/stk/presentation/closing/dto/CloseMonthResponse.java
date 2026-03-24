package team2.stk.presentation.closing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.closing.CloseMonthUseCase;
import team2.stk.domain.closing.ClosingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CloseMonthResponse {
    private final UUID closingId;
    private final UUID itemId;
    private final String itemCode;
    private final String itemName;
    private final String closingYm;
    private final ClosingStatus status;
    private final int openingStock;
    private final int inboundQty;
    private final int outboundQty;
    private final int closingStock;
    private final String message;
    private final LocalDateTime closedAt;

    public static CloseMonthResponse from(CloseMonthUseCase.CloseResult result) {
        return new CloseMonthResponse(
                result.closing().getId(),
                result.closing().getItem().getId(),
                result.closing().getItemCode(),
                result.closing().getItemName(),
                result.closing().getClosingYm(),
                result.closing().getStatus(),
                result.closing().getOpeningStock(),
                result.closing().getInboundQty(),
                result.closing().getOutboundQty(),
                result.closing().getClosingStock(),
                result.message(),
                result.closing().getClosedAt()
        );
    }
}