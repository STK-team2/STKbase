package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.ItemNotFoundException;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.movement.exception.InsufficientStockException;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.ChangeHistoryRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.infrastructure.push.WebPushService;
import team2.stk.shared.util.ScreenName;
import team2.stk.shared.util.SecurityUtil;
import team2.stk.shared.util.TableName;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterOutboundUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final WebPushService webPushService;

    @Transactional
    public StockMovement execute(String site, UUID itemId, int quantity, LocalDate movementDate,
                                 String reference, String note, boolean allowNegativeStock) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

        int currentStock = stockMovementRepository.calculateCurrentStock(itemId);

        if (!allowNegativeStock && currentStock < quantity) {
            throw new InsufficientStockException(item.getItemCode(), currentStock, quantity);
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        StockMovement stockMovement = new StockMovement(
                item, user, site, MovementType.OUTBOUND, quantity, movementDate, reference, note);
        StockMovement saved = stockMovementRepository.save(stockMovement);

        // 히스토리 저장
        Map<String, Object> snapshot = buildSnapshot(item, quantity, movementDate, site, reference, note);
        changeHistoryRepository.save(new ChangeHistory(
                user, TableName.STOCK_MOVEMENT, ScreenName.OUTBOUND,
                saved.getId(), "CREATE", null, snapshot));

        // 저재고 알림
        int stockAfter = currentStock - quantity;
        if (item.isLowStock(stockAfter)) {
            webPushService.notifyAdmins(
                    "저재고 알림",
                    item.getItemName() + " 재고가 " + stockAfter + "개로 임계값(" + item.getLowStockThreshold() + "개) 이하입니다.");
        }

        return saved;
    }

    private Map<String, Object> buildSnapshot(Item item, int quantity, LocalDate movementDate,
                                               String site, String reference, String note) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("itemCode", item.getItemCode());
        map.put("itemName", item.getItemName());
        map.put("location", item.getLocation());
        map.put("quantity", quantity);
        map.put("movementDate", movementDate.toString());
        map.put("site", site);
        map.put("reference", reference);
        map.put("note", note);
        return map;
    }
}