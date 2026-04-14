package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository.MonthlyMovementTotal;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMonthlyTrendUseCase {

    private static final int MONTHS = 12;

    private final StockMovementRepository stockMovementRepository;

    public List<MonthlyResult> execute() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(MONTHS - 1);

        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<MonthlyMovementTotal> totals = stockMovementRepository.getMonthlyTrend(startDate, endDate);
        Map<String, MonthlyMovementTotal> totalMap = totals.stream()
                .collect(Collectors.toMap(MonthlyMovementTotal::getMonth, t -> t));

        List<MonthlyResult> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (YearMonth ym = startMonth; !ym.isAfter(currentMonth); ym = ym.plusMonths(1)) {
            String month = ym.format(formatter);
            MonthlyMovementTotal total = totalMap.get(month);
            if (total != null) {
                results.add(new MonthlyResult(month, total.getInboundTotal(), total.getOutboundTotal()));
            } else {
                results.add(new MonthlyResult(month, 0, 0));
            }
        }

        return results;
    }

    public record MonthlyResult(String month, long inboundTotal, long outboundTotal) {}
}
