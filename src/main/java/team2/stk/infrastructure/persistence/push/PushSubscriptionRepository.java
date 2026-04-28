package team2.stk.infrastructure.persistence.push;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.push.PushSubscription;
import team2.stk.domain.user.Role;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PushSubscriptionRepository {

    private final PushSubscriptionJpaRepository jpaRepository;

    public PushSubscription save(PushSubscription subscription) {
        return jpaRepository.save(subscription);
    }

    public void deleteByEndpoint(String endpoint) {
        jpaRepository.deleteByEndpoint(endpoint);
    }

    public List<PushSubscription> findAllAdmins() {
        return jpaRepository.findAllByUserRole(Role.ADMIN);
    }
}
