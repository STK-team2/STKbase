package team2.stk.application.closing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.closing.Closing;
import team2.stk.domain.closing.ClosingStatus;
import team2.stk.infrastructure.persistence.closing.ClosingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetClosingStockUseCase {

    private final ClosingRepository closingRepository;

    @Transactional(readOnly = true)
    public List<ClosingStockResult> execute(String closingYm, ClosingStatus status) {
        List<Closing> closings;

        if (closingYm != null) {
            closings = closingRepository.findByClosingYmAndStatus(closingYm, status);
        } else {
            closings = closingRepository.findByStatus(status);
        }

        return closings.stream()
                .map(closing -> new ClosingStockResult(
                        closing.getId(),
                        closing.getItem().getId(),
                        closing.getItemCode(),
                        closing.getItemName(),
                        closing.getItem().getBoxNumber(),
                        closing.getItem().getLocation(),
                        closing.getClosingYm(),
                        closing.getStatus(),
                        closing.getOpeningStock(),
                        closing.getInboundQty(),
                        closing.getOutboundQty(),
                        closing.getClosingStock(),
                        closing.getUser().getName(),
                        closing.getClosedAt()
                ))
                .toList();
    }

    public record ClosingStockResult(
            java.util.UUID closingId,
            java.util.UUID itemId,
            String itemCode,
            String itemName,
            String boxNumber,
            String location,
            String closingYm,
            ClosingStatus status,
            int openingStock,
            int inboundQty,
            int outboundQty,
            int closingStock,
            String userName,
            java.time.LocalDateTime closedAt
    ) {}
}