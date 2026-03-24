package team2.stk.presentation.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.stock.GetLedgerUseCase;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LedgerResponse {
    private final UUID itemId;
    private final String itemCode;
    private final String itemName;
    private final String boxNumber;
    private final String location;
    private final int openingStock;   // 기초재고
    private final int inboundQty;     // 입고수량
    private final int outboundQty;    // 출고수량
    private final int closingStock;   // 기말재고

    public static LedgerResponse from(GetLedgerUseCase.LedgerResult result) {
        return new LedgerResponse(
                result.itemId(),
                result.itemCode(),
                result.itemName(),
                result.boxNumber(),
                result.location(),
                result.openingStock(),
                result.inboundQty(),
                result.outboundQty(),
                result.closingStock()
        );
    }
}