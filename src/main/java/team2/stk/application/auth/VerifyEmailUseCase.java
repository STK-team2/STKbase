package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.EmailVerification;
import team2.stk.domain.user.User;
import team2.stk.domain.user.exception.InvalidVerificationCodeException;
import team2.stk.infrastructure.persistence.user.EmailVerificationRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCase {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(String email, String code) {
        EmailVerification emailVerification = emailVerificationRepository.findLatestByEmail(email)
                .orElseThrow(InvalidVerificationCodeException::new);

        if (!emailVerification.isValid(code)) {
            throw new InvalidVerificationCodeException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidVerificationCodeException::new);

        user.verify();
        userRepository.save(user);
    }
}