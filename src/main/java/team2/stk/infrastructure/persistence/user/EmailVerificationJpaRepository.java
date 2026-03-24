package team2.stk.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.stk.domain.user.EmailVerification;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByEmailOrderByCreatedAtDesc(String email);
}