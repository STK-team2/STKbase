package team2.stk.application.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.stk.infrastructure.persistence.closing.ClosingRepository;
import team2.stk.infrastructure.persistence.item.ItemRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GetClosingStatusUseCase {

    private static final DateTimeFormatter YM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ClosingRepository closingRepository;
    private final ItemRepository itemRepository;

    public ClosingStatusResult execute() {
        String currentYm = LocalDate.now().format(YM_FORMAT);

        long totalItems = itemRepository.countActive();
        long closedCount = closingRepository.countClosedByClosingYm(currentYm);
        long unclosedCount = totalItems - closedCount;
        boolean isClosed = unclosedCount <= 0;
        long totalClosedAll = closingRepository.countAllClosed();

        return new ClosingStatusResult(currentYm, isClosed, closedCount, unclosedCount, totalClosedAll);
    }

    public record ClosingStatusResult(
            String closingYm,
            boolean isClosed,
            long closedCount,
            long unclosedCount,
            long totalClosedAll
    ) {}
}
