package team2.stk.application.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetLedgerUseCase {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<LedgerResult> execute(LocalDate startDate, LocalDate endDate) {
        List<Item> items = itemRepository.findAllActive();

        return items.stream()
                .map(item -> calculateLedger(item, startDate, endDate))
                .toList();
    }

    private LedgerResult calculateLedger(Item item, LocalDate startDate, LocalDate endDate) {
        UUID itemId = item.getId();

        // 기초재고 = 시작일 이전까지의 누적재고
        int openingStock = 0;
        if (startDate != null) {
            // 시작일 이전까지의 모든 입출고 이력으로 기초재고 계산
            StockMovementJpaRepository.MovementSummary beforeSummary =
                stockMovementRepository.getMovementSummary(itemId, LocalDate.of(1900, 1, 1), startDate.minusDays(1));
            openingStock = beforeSummary.getInboundQty() - beforeSummary.getOutboundQty();
        }

        // 해당 기간의 입출고 수량
        StockMovementJpaRepository.MovementSummary periodSummary =
            stockMovementRepository.getMovementSummary(itemId, startDate, endDate);

        int inboundQty = periodSummary.getInboundQty();
        int outboundQty = periodSummary.getOutboundQty();

        // 기말재고 = 기초재고 + 입고 - 출고
        int closingStock = openingStock + inboundQty - outboundQty;

        return new LedgerResult(
                itemId,
                item.getItemCode(),
                item.getItemName(),
                item.getBoxNumber(),
                item.getLocation(),
                openingStock,
                inboundQty,
                outboundQty,
                closingStock
        );
    }

    public record LedgerResult(
            UUID itemId,
            String itemCode,
            String itemName,
            String boxNumber,
            String location,
            int openingStock,   // 기초재고
            int inboundQty,     // 입고수량
            int outboundQty,    // 출고수량
            int closingStock    // 기말재고
    ) {}
}