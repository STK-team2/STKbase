package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.movement.exception.MovementNotFoundException;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
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
public class DeleteMovementUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(UUID movementId) {
        StockMovement movement = stockMovementRepository.findByIdActive(movementId)
                .orElseThrow(() -> new MovementNotFoundException(movementId.toString()));

        Map<String, Object> beforeValue = new LinkedHashMap<>();
        beforeValue.put("itemCode", movement.getItem().getItemCode());
        beforeValue.put("site", movement.getSite());
        beforeValue.put("type", movement.getType().name());
        beforeValue.put("quantity", movement.getQuantity());
        beforeValue.put("movementDate", movement.getMovementDate().toString());
        beforeValue.put("reference", movement.getReference());
        beforeValue.put("note", movement.getNote());

        movement.delete();
        stockMovementRepository.save(movement);

        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        String screenName = movement.getType().name().contains("INBOUND") ? ScreenName.INBOUND : ScreenName.OUTBOUND;
        ChangeHistory history = new ChangeHistory(
                currentUser, TableName.STOCK_MOVEMENT, screenName,
                movementId, "DELETE", beforeValue, null
        );
        changeHistoryRepository.save(history);
    }
}