package team2.stk.presentation.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team2.stk.domain.item.Item;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ItemResponse {
    private final UUID id;
    private final String itemCode;
    private final String itemName;
    private final String boxNumber;
    private final String location;
    private final LocalDateTime createdAt;

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getItemCode(),
                item.getItemName(),
                item.getBoxNumber(),
                item.getLocation(),
                item.getCreatedAt()
        );
    }
}