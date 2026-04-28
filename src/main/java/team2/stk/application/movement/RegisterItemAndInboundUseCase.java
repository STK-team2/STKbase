package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.application.item.RegisterItemUseCase;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.SecurityUtil;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterItemAndInboundUseCase {

    private final RegisterItemUseCase registerItemUseCase;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Transactional
    public RegisterResult execute(String itemCode, String itemName, String boxNumber, String location,
                                  String site, int quantity, LocalDate movementDate, String reference, String note) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        // 1. 새로운 자재 등록
        Item item = registerItemUseCase.execute(itemCode, itemName, boxNumber, location, null, null);

        // 2. 입고 등록
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        StockMovement stockMovement = new StockMovement(
                item, user, site, MovementType.INBOUND, quantity, movementDate, reference, note);

        StockMovement savedMovement = stockMovementRepository.save(stockMovement);

        return new RegisterResult(item, savedMovement);
    }

    public record RegisterResult(Item item, StockMovement stockMovement) {}
}