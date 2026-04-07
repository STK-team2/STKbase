package team2.stk.infrastructure.persistence.closing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.stk.domain.closing.Closing;
import team2.stk.domain.closing.ClosingStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClosingJpaRepository extends JpaRepository<Closing, UUID> {

    @Query("SELECT c FROM Closing c WHERE c.id = :id")
    Optional<Closing> findByIdWithoutStatus(@Param("id") UUID id);

    @Query("SELECT c FROM Closing c " +
           "WHERE c.item.id = :itemId AND c.closingYm = :closingYm " +
           "ORDER BY c.closedAt DESC")
    List<Closing> findByItemIdAndClosingYm(@Param("itemId") UUID itemId, @Param("closingYm") String closingYm);

    @Query("SELECT c FROM Closing c " +
           "WHERE c.closingYm = :closingYm " +
           "AND (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.item.itemCode")
    List<Closing> findByClosingYmAndStatus(@Param("closingYm") String closingYm, @Param("status") ClosingStatus status);

    @Query("SELECT c FROM Closing c " +
           "WHERE (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.closingYm DESC, c.item.itemCode")
    List<Closing> findByStatus(@Param("status") ClosingStatus status);

    // 특정 아이템의 최신 마감 여부 확인
    @Query("SELECT c FROM Closing c " +
           "WHERE c.item.id = :itemId AND c.closingYm = :closingYm " +
           "AND c.status = 'CLOSED'")
    Optional<Closing> findActiveClosingByItemAndYm(@Param("itemId") UUID itemId, @Param("closingYm") String closingYm);

    // 마감 순서 검증을 위한 쿼리 - 해당 월보다 이전 월 중 마감되지 않은 월이 있는지 확인
    @Query("SELECT COUNT(c) > 0 FROM Closing c " +
           "WHERE c.item.id = :itemId " +
           "AND c.closingYm < :currentYm " +
           "AND c.status = 'CLOSED'")
    boolean hasUnclosedPreviousMonth(@Param("itemId") UUID itemId, @Param("currentYm") String currentYm);

    @Query("SELECT COUNT(c) > 0 FROM Closing c WHERE c.closingYm = :closingYm")
    boolean existsByClosingYm(@Param("closingYm") String closingYm);

    // 전체 아이템 중 특정 월에 마감되지 않은 아이템이 있는지 확인
    @Query("SELECT COUNT(DISTINCT i.id) FROM Item i " +
           "WHERE i.deletedAt IS NULL " +
           "AND NOT EXISTS (" +
           "    SELECT c FROM Closing c " +
           "    WHERE c.item.id = i.id " +
           "    AND c.closingYm = :closingYm " +
           "    AND c.status = 'CLOSED'" +
           ")")
    long countUnclosedItems(@Param("closingYm") String closingYm);
}