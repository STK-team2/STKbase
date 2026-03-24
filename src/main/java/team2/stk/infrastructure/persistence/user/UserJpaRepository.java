package team2.stk.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.stk.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}