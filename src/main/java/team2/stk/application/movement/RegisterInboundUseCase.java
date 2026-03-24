package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.domain.item.exception.ItemNotFoundException;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.SecurityUtil;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterInboundUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public StockMovement execute(String site, UUID itemId, int quantity, LocalDate movementDate, String reference, String note) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        StockMovement stockMovement = new StockMovement(
                item, user, site, MovementType.INBOUND, quantity, movementDate, reference, note);

        return stockMovementRepository.save(stockMovement);
    }
}