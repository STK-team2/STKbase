package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCase {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;

    public SummaryResult execute() {
        LocalDate today = LocalDate.now();

        long todayInbound = stockMovementRepository.countTodayInbound(today);
        long todayOutbound = stockMovementRepository.countTodayOutbound(today);
        long totalItems = itemRepository.countActive();

        return new SummaryResult(todayInbound, todayOutbound, totalItems);
    }

    public record SummaryResult(long todayInbound, long todayOutbound, long totalItems) {}
}
