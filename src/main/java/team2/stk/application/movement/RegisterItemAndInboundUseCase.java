package team2.stk.application.movement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.application.item.RegisterItemUseCase;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.StockMovement;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegisterItemAndInboundUseCase {

    private final RegisterItemUseCase registerItemUseCase;
    private final RegisterInboundUseCase registerInboundUseCase;

    @Transactional
    public RegisterResult execute(String itemCode, String itemName, String boxNumber, String location,
                                  String site, int quantity, LocalDate movementDate, String reference, String note) {
        Item item = registerItemUseCase.execute(itemCode, itemName, boxNumber, location, null, null);
        StockMovement savedMovement = registerInboundUseCase.execute(
                site, item.getId(), quantity, movementDate, reference, note);

        return new RegisterResult(item, savedMovement);
    }

    public record RegisterResult(Item item, StockMovement stockMovement) {}
}