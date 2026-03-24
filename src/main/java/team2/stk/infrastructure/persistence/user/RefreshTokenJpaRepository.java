package team2.stk.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.stk.domain.user.RefreshToken;
import team2.stk.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}