package team2.stk.application.closing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.closing.Closing;
import team2.stk.domain.closing.ClosingStatus;
import team2.stk.domain.closing.exception.ClosingOrderViolatedException;
import team2.stk.domain.item.Item;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.closing.ClosingRepository;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementJpaRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.util.SecurityUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloseMonthUseCase {

    private final ClosingRepository closingRepository;
    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<CloseResult> execute(String closingYm) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 순서 검증: 이전 월이 모두 마감되었는지 확인
        validateClosingOrder(closingYm);

        List<Item> items = itemRepository.findAllActive();
        List<CloseResult> results = new ArrayList<>();

        for (Item item : items) {
            CloseResult result = processItemClosing(item, user, closingYm);
            results.add(result);
        }

        return results;
    }

    private void validateClosingOrder(String closingYm) {
        // 간단한 검증 로직: 현재 월 이전에 마감되지 않은 월이 있는지 확인
        // 실제로는 더 정교한 로직이 필요할 수 있음
        String previousYm = getPreviousMonth(closingYm);
        if (previousYm != null) {
            long unclosedCount = closingRepository.countUnclosedItems(previousYm);
            if (unclosedCount > 0) {
                throw new ClosingOrderViolatedException(closingYm);
            }
        }
    }

    private CloseResult processItemClosing(Item item, User user, String closingYm) {
        UUID itemId = item.getId();

        // 이미 마감된 경우 처리
        Optional<Closing> existingClosing = closingRepository.findActiveClosingByItemAndYm(itemId, closingYm);
        if (existingClosing.isPresent()) {
            Closing closing = existingClosing.get();
            if (closing.getStatus() == ClosingStatus.CANCELLED) {
                // 취소된 마감을 재마감
                closing.reopen();
                closingRepository.save(closing);
                return new CloseResult(closing, "재마감");
            } else {
                // 이미 마감됨
                return new CloseResult(closing, "이미 마감됨");
            }
        }

        // 신규 마감 처리
        LocalDate startDate = LocalDate.parse(closingYm + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 기초재고 계산 (이전 월까지의 누적)
        int openingStock = calculateOpeningStock(itemId, startDate);

        // 해당 월의 입출고 수량 집계
        StockMovementJpaRepository.MovementSummary summary =
                stockMovementRepository.getMovementSummary(itemId, startDate, endDate);

        int inboundQty = summary.getInboundQty();
        int outboundQty = summary.getOutboundQty();

        Closing closing = new Closing(item, user, closingYm, openingStock, inboundQty, outboundQty);
        closingRepository.save(closing);

        return new CloseResult(closing, "신규 마감");
    }

    private int calculateOpeningStock(UUID itemId, LocalDate startDate) {
        LocalDate previousDay = startDate.minusDays(1);
        StockMovementJpaRepository.MovementSummary beforeSummary =
                stockMovementRepository.getMovementSummary(
                        itemId,
                        LocalDate.of(1900, 1, 1),
                        previousDay
                );
        return beforeSummary.getInboundQty() - beforeSummary.getOutboundQty();
    }

    private String getPreviousMonth(String closingYm) {
        try {
            LocalDate date = LocalDate.parse(closingYm + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate previousMonth = date.minusMonths(1);
            return previousMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return null;
        }
    }

    public record CloseResult(Closing closing, String message) {}
}