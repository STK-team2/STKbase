package team2.stk.application.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.DuplicateItemCodeException;
import team2.stk.infrastructure.persistence.item.ItemRepository;

@Service
@RequiredArgsConstructor
public class RegisterItemUseCase {

    private final ItemRepository itemRepository;

    @Transactional
    public Item execute(String itemCode, String itemName, String boxNumber, String location) {
        if (itemRepository.existsByItemCodeActive(itemCode)) {
            throw new DuplicateItemCodeException(itemCode);
        }

        Item item = new Item(itemCode, itemName, boxNumber, location);
        return itemRepository.save(item);
    }
}