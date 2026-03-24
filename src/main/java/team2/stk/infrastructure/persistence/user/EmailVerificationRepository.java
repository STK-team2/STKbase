package team2.stk.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.user.EmailVerification;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepository {

    private final EmailVerificationJpaRepository emailVerificationJpaRepository;

    public Optional<EmailVerification> findLatestByEmail(String email) {
        return emailVerificationJpaRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    public EmailVerification save(EmailVerification emailVerification) {
        return emailVerificationJpaRepository.save(emailVerification);
    }
}