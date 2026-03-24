package team2.stk.presentation.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.application.stock.GetCurrentStockUseCase;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CurrentStockResponse {
    private final UUID itemId;
    private final String itemCode;
    private final String itemName;
    private final String boxNumber;
    private final String location;
    private final int currentStock;

    public static CurrentStockResponse from(GetCurrentStockUseCase.CurrentStockResult result) {
        return new CurrentStockResponse(
                result.itemId(),
                result.itemCode(),
                result.itemName(),
                result.boxNumber(),
                result.location(),
                result.currentStock()
        );
    }
}