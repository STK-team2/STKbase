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

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateMovementUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public StockMovement execute(UUID movementId, String site, int quantity, LocalDate movementDate, String reference, String note) {
        StockMovement movement = stockMovementRepository.findByIdActive(movementId)
                .orElseThrow(() -> new MovementNotFoundException(movementId.toString()));

        Map<String, Object> beforeValue = toSnapshot(movement);

        movement.update(site, quantity, movementDate, reference, note);
        StockMovement saved = stockMovementRepository.save(movement);

        Map<String, Object> afterValue = toSnapshot(saved);

        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        String screenName = movement.getType().name().contains("INBOUND") ? ScreenName.INBOUND : ScreenName.OUTBOUND;
        ChangeHistory history = new ChangeHistory(
                currentUser, TableName.STOCK_MOVEMENT, screenName,
                movementId, "UPDATE", beforeValue, afterValue
        );
        changeHistoryRepository.save(history);

        return saved;
    }

    private Map<String, Object> toSnapshot(StockMovement m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("site", m.getSite());
        map.put("quantity", m.getQuantity());
        map.put("movementDate", m.getMovementDate().toString());
        map.put("reference", m.getReference());
        map.put("note", m.getNote());
        return map;
    }
}