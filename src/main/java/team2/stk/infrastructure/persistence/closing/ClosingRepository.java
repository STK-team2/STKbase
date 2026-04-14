package team2.stk.infrastructure.persistence.closing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.closing.Closing;
import team2.stk.domain.closing.ClosingStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClosingRepository {

    private final ClosingJpaRepository closingJpaRepository;

    public Optional<Closing> findById(UUID id) {
        return closingJpaRepository.findByIdWithoutStatus(id);
    }

    public List<Closing> findByItemIdAndClosingYm(UUID itemId, String closingYm) {
        return closingJpaRepository.findByItemIdAndClosingYm(itemId, closingYm);
    }

    public List<Closing> findByClosingYmAndStatus(String closingYm, ClosingStatus status) {
        return closingJpaRepository.findByClosingYmAndStatus(closingYm, status);
    }

    public List<Closing> findByStatus(ClosingStatus status) {
        return closingJpaRepository.findByStatus(status);
    }

    public Optional<Closing> findActiveClosingByItemAndYm(UUID itemId, String closingYm) {
        return closingJpaRepository.findActiveClosingByItemAndYm(itemId, closingYm);
    }

    public boolean hasUnclosedPreviousMonth(UUID itemId, String currentYm) {
        return closingJpaRepository.hasUnclosedPreviousMonth(itemId, currentYm);
    }

    public long countUnclosedItems(String closingYm) {
        return closingJpaRepository.countUnclosedItems(closingYm);
    }

    public boolean existsByClosingYm(String closingYm) {
        return closingJpaRepository.existsByClosingYm(closingYm);
    }

    public long countClosedByClosingYm(String closingYm) {
        return closingJpaRepository.countClosedByClosingYm(closingYm);
    }

    public long countAllClosed() {
        return closingJpaRepository.countAllClosed();
    }

    public Closing save(Closing closing) {
        return closingJpaRepository.save(closing);
    }
}