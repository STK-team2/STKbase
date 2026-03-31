package team2.stk.application.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetItemsUseCase {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<ItemResult> execute(String query) {
        List<Item> items;
        if (query == null || query.trim().isEmpty()) {
            items = itemRepository.findAllActive();
        } else {
            items = itemRepository.searchActive(query.trim());
        }

        return items.stream()
                .map(item -> new ItemResult(
                        item.getId(),
                        item.getItemCode(),
                        item.getItemName(),
                        item.getBoxNumber(),
                        item.getLocation(),
                        item.getCreatedAt()
                ))
                .toList();
    }

    public record ItemResult(
            UUID itemId,
            String itemCode,
            String itemName,
            String boxNumber,
            String location,
            java.time.LocalDateTime createdAt
    ) {}
}
