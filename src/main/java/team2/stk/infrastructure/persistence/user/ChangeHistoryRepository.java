package team2.stk.infrastructure.persistence.user;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import team2.stk.domain.user.ChangeHistory;
import team2.stk.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChangeHistoryRepository {

    private final ChangeHistoryJpaRepository changeHistoryJpaRepository;

    public ChangeHistory save(ChangeHistory changeHistory) {
        return changeHistoryJpaRepository.save(changeHistory);
    }

    public List<ChangeHistory> searchChangeHistory(String tableName, String screenName,
                                                   LocalDateTime startDate, LocalDateTime endDate,
                                                   String query) {
        Specification<ChangeHistory> spec = (root, cq, cb) -> {
            Join<ChangeHistory, User> user = root.join("user", JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();

            if (tableName != null && !tableName.isBlank()) {
                predicates.add(cb.equal(root.get("tableName"), tableName));
            }
            if (screenName != null && !screenName.isBlank()) {
                predicates.add(cb.equal(root.get("screenName"), screenName));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("changedAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("changedAt"), endDate));
            }
            if (query != null && !query.isBlank()) {
                String q = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(user.get("name")), q),
                        cb.like(cb.lower(root.get("tableName")), q),
                        cb.like(cb.lower(root.get("action")), q)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return changeHistoryJpaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "changedAt"));
    }

    public List<ChangeHistory> findByRecordIdAndTableName(UUID recordId, String tableName) {
        return changeHistoryJpaRepository.findByRecordIdAndTableName(recordId, tableName);
    }

    public List<ChangeHistory> findByUserId(UUID userId) {
        return changeHistoryJpaRepository.findByUserId(userId);
    }
}