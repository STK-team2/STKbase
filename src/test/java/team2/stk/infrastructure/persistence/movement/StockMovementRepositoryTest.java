package team2.stk.infrastructure.persistence.movement;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.StkApplication;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.MovementType;
import team2.stk.domain.movement.StockMovement;
import team2.stk.domain.user.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = StkApplication.class)
@ActiveProfiles("test")
@Transactional
class StockMovementRepositoryTest {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("검색 필터가 모두 비어 있어도 입출고 내역을 정상 조회한다")
    void searchMovements_withoutFilters_returnsAllActiveMovements() {
        Item cable = new Item("ITM-001", "LAN Cable", "BOX-1", "A-01");
        entityManager.persist(cable);
        User user = new User("user1@test.com", "tester1", "hashed-password");
        entityManager.persist(user);

        entityManager.persist(new StockMovement(
                cable, user, "SEOUL", MovementType.INBOUND, 10,
                LocalDate.of(2026, 4, 20), "REF-1", "note-1"
        ));
        entityManager.persist(new StockMovement(
                cable, user, "SEOUL", MovementType.OUTBOUND, 4,
                LocalDate.of(2026, 4, 21), "REF-2", "note-2"
        ));
        entityManager.flush();
        entityManager.clear();

        List<StockMovement> result = stockMovementRepository.searchMovements(null, null, null, "");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(StockMovement::getMovementDate)
                .containsExactly(LocalDate.of(2026, 4, 21), LocalDate.of(2026, 4, 20));
    }

    @Test
    @DisplayName("검색 필터가 있을 때 해당 조건에 맞는 입출고 내역만 조회한다")
    void searchMovements_withFilters_appliesConditions() {
        Item cable = new Item("ITM-001", "LAN Cable", "BOX-1", "A-01");
        Item switchItem = new Item("ITM-002", "Network Switch", "BOX-2", "A-02");
        User user = new User("user2@test.com", "tester2", "hashed-password");
        entityManager.persist(cable);
        entityManager.persist(switchItem);
        entityManager.persist(user);

        entityManager.persist(new StockMovement(
                cable, user, "SEOUL", MovementType.INBOUND, 10,
                LocalDate.of(2026, 4, 18), "REF-1", "note-1"
        ));
        entityManager.persist(new StockMovement(
                cable, user, "SEOUL", MovementType.OUTBOUND, 2,
                LocalDate.of(2026, 4, 20), "REF-2", "note-2"
        ));
        entityManager.persist(new StockMovement(
                switchItem, user, "BUSAN", MovementType.OUTBOUND, 1,
                LocalDate.of(2026, 4, 20), "REF-3", "note-3"
        ));
        entityManager.flush();
        entityManager.clear();

        List<StockMovement> result = stockMovementRepository.searchMovements(
                MovementType.OUTBOUND,
                LocalDate.of(2026, 4, 19),
                LocalDate.of(2026, 4, 20),
                "switch"
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItem().getItemCode()).isEqualTo("ITM-002");
        assertThat(result.getFirst().getType()).isEqualTo(MovementType.OUTBOUND);
    }
}
