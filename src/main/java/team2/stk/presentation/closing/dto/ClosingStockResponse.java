package team2.stk.presentation.closing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.closing.GetClosingStockUseCase;
import team2.stk.domain.closing.ClosingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClosingStockResponse {
    private final UUID closingId;
    private final UUID itemId;
    private final String itemCode;
    private final String itemName;
    private final String boxNumber;
    private final String location;
    private final String closingYm;
    private final ClosingStatus status;
    private final int openingStock;   // 기초재고
    private final int inboundQty;     // 입고수량
    private final int outboundQty;    // 출고수량
    private final int closingStock;   // 기말재고
    private final String userName;    // 마감자
    private final LocalDateTime closedAt; // 마감일시

    public static ClosingStockResponse from(GetClosingStockUseCase.ClosingStockResult result) {
        return new ClosingStockResponse(
                result.closingId(),
                result.itemId(),
                result.itemCode(),
                result.itemName(),
                result.boxNumber(),
                result.location(),
                result.closingYm(),
                result.status(),
                result.openingStock(),
                result.inboundQty(),
                result.outboundQty(),
                result.closingStock(),
                result.userName(),
                result.closedAt()
        );
    }
}