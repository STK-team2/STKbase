package team2.stk.application.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCurrentStockUseCase {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<CurrentStockResult> execute() {
        List<Item> items = itemRepository.findAllActive();

        // 단일 쿼리로 모든 아이템의 재고를 한 번에 조회 (N+1 방지)
        Map<UUID, Integer> stockMap = stockMovementRepository.calculateAllCurrentStock().stream()
                .collect(Collectors.toMap(
                        StockMovementJpaRepository.ItemStockProjection::getItemId,
                        StockMovementJpaRepository.ItemStockProjection::getStock
                ));

        return items.stream()
                .map(item -> new CurrentStockResult(
                        item.getId(),
                        item.getItemCode(),
                        item.getItemName(),
                        item.getBoxNumber(),
                        item.getLocation(),
                        stockMap.getOrDefault(item.getId(), 0)
                ))
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