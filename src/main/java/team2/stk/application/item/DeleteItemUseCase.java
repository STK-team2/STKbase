package team2.stk.application.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.ItemNotFoundException;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.user.ChangeHistoryRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.SecurityUtil;
import team2.stk.shared.util.ScreenName;
import team2.stk.shared.util.TableName;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteItemUseCase {

    private final ItemRepository itemRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(UUID itemId) {
        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

        Map<String, Object> beforeValue = new LinkedHashMap<>();
        beforeValue.put("itemCode", item.getItemCode());
        beforeValue.put("itemName", item.getItemName());
        beforeValue.put("boxNumber", item.getBoxNumber());
        beforeValue.put("location", item.getLocation());

        item.delete();
        itemRepository.save(item);

        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        ChangeHistory history = new ChangeHistory(
                currentUser, TableName.ITEMS, ScreenName.ITEM,
                itemId, "DELETE", beforeValue, null
        );
        changeHistoryRepository.save(history);
    }
}