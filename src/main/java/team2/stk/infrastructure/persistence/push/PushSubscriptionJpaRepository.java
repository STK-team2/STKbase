package team2.stk.infrastructure.persistence.push;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team2.stk.domain.push.PushSubscription;
import team2.stk.domain.user.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscription, UUID> {

    Optional<PushSubscription> findByEndpoint(String endpoint);

    void deleteByEndpoint(String endpoint);

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.user.role = :role")
    List<PushSubscription> findAllByUserRole(Role role);
}
