package team2.stk.application.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Category;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.DuplicateItemCodeException;
import team2.stk.infrastructure.persistence.item.CategoryRepository;
import team2.stk.infrastructure.persistence.item.ItemRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterItemUseCase {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Item execute(String itemCode, String itemName, String boxNumber, String location,
                        UUID categoryId, Integer lowStockThreshold) {
        if (itemRepository.existsByItemCodeActive(itemCode)) {
            throw new DuplicateItemCodeException(itemCode);
        }

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElse(null);
        }

        Item item = new Item(itemCode, itemName, boxNumber, location, category, lowStockThreshold);
        return itemRepository.save(item);
    }
}