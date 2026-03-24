package team2.stk.application.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCurrentStockUseCase {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<CurrentStockResult> execute() {
        List<Item> items = itemRepository.findAllActive();

        return items.stream()
                .map(item -> {
                    int currentStock = stockMovementRepository.calculateCurrentStock(item.getId());
                    return new CurrentStockResult(
                            item.getId(),
                            item.getItemCode(),
                            item.getItemName(),
                            item.getBoxNumber(),
                            item.getLocation(),
                            currentStock
                    );
                })
                .toList();
    }

    public record CurrentStockResult(
            java.util.UUID itemId,
            String itemCode,
            String itemName,
            String boxNumber,
            String location,
            int currentStock
    ) {}
}