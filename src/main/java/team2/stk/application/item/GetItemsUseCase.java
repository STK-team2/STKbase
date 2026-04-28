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
    public List<ItemResult> execute(String query, UUID categoryId) {
        List<Item> items;
        String q = (query == null) ? "" : query.trim();
        if (categoryId != null) {
            items = itemRepository.searchActiveByCategory(categoryId, q);
        } else if (q.isEmpty()) {
            items = itemRepository.findAllActive();
        } else {
            items = itemRepository.searchActive(q);
        }

        return items.stream().map(ItemResult::from).toList();
    }

    public record ItemResult(
            UUID itemId,
            String itemCode,
            String itemName,
            String boxNumber,
            String location,
            String categoryName,
            String imageUrl,
            Integer lowStockThreshold,
            java.time.LocalDateTime createdAt
    ) {
        public static ItemResult from(team2.stk.domain.item.Item item) {
            return new ItemResult(
                    item.getId(),
                    item.getItemCode(),
                    item.getItemName(),
                    item.getBoxNumber(),
                    item.getLocation(),
                    item.getCategory() != null ? item.getCategory().getName() : null,
                    item.getImageUrl(),
                    item.getLowStockThreshold(),
                    item.getCreatedAt()
            );
        }
    }
}
