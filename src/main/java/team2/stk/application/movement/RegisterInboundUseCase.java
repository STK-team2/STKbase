package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.ItemNotFoundException;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.ChangeHistoryRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.ScreenName;
import team2.stk.shared.util.SecurityUtil;
import team2.stk.shared.util.TableName;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterInboundUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ChangeHistoryRepository changeHistoryRepository;

    @Transactional
    public StockMovement execute(String site, UUID itemId, int quantity, LocalDate movementDate,
                                 String reference, String note) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        StockMovement stockMovement = new StockMovement(
                item, user, site, MovementType.INBOUND, quantity, movementDate, reference, note);
        StockMovement saved = stockMovementRepository.save(stockMovement);

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("itemCode", item.getItemCode());
        snapshot.put("itemName", item.getItemName());
        snapshot.put("location", item.getLocation());
        snapshot.put("quantity", quantity);
        snapshot.put("movementDate", movementDate.toString());
        snapshot.put("site", site);
        snapshot.put("reference", reference);
        snapshot.put("note", note);

        changeHistoryRepository.save(new ChangeHistory(
                user, TableName.STOCK_MOVEMENT, ScreenName.INBOUND,
                saved.getId(), "CREATE", null, snapshot));

        return saved;
    }
}