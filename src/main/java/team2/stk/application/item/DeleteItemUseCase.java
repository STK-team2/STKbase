package team2.stk.application.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.ItemNotFoundException;
import team2.stk.infrastructure.persistence.item.ItemRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteItemUseCase {

    private final ItemRepository itemRepository;

    @Transactional
    public void execute(UUID itemId) {
        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

        item.delete();
        itemRepository.save(item);
    }
}