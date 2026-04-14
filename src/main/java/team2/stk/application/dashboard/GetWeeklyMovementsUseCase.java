package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository.DailyMovementCount;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWeeklyMovementsUseCase {

    private final StockMovementRepository stockMovementRepository;

    public List<DailyResult> execute() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate friday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        List<DailyMovementCount> counts = stockMovementRepository.countDailyMovements(monday, friday);
        Map<LocalDate, DailyMovementCount> countMap = counts.stream()
                .collect(Collectors.toMap(DailyMovementCount::getDate, c -> c));

        List<DailyResult> results = new ArrayList<>();
        for (LocalDate date = monday; !date.isAfter(friday); date = date.plusDays(1)) {
            DailyMovementCount count = countMap.get(date);
            if (count != null) {
                results.add(new DailyResult(date, count.getInboundCount(), count.getOutboundCount()));
            } else {
                results.add(new DailyResult(date, 0, 0));
            }
        }

        return results;
    }

    public record DailyResult(LocalDate date, long inboundCount, long outboundCount) {}
}
