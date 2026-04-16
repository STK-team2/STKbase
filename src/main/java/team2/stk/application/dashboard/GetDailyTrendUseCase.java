package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository.DailyMovementTotal;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDailyTrendUseCase {

    private static final int DEFAULT_DAYS = 30;

    private final StockMovementRepository stockMovementRepository;

    public List<DailyResult> execute(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            to = LocalDate.now();
            from = to.minusDays(DEFAULT_DAYS - 1);
        }

        List<DailyMovementTotal> totals = stockMovementRepository.getDailyTrend(from, to);
        Map<LocalDate, DailyMovementTotal> totalMap = totals.stream()
                .collect(Collectors.toMap(DailyMovementTotal::getDate, t -> t));

        List<DailyResult> results = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            DailyMovementTotal total = totalMap.get(date);
            if (total != null) {
                results.add(new DailyResult(date, total.getInboundTotal(), total.getOutboundTotal()));
            } else {
                results.add(new DailyResult(date, 0, 0));
            }
        }

        return results;
    }

    public record DailyResult(LocalDate date, long inboundTotal, long outboundTotal) {}
}
