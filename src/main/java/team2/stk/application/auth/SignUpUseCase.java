package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.EmailVerification;
import team2.stk.domain.user.User;
import team2.stk.domain.user.exception.EmailAlreadyExistsException;
import team2.stk.infrastructure.mail.EmailService;
import team2.stk.infrastructure.persistence.user.EmailVerificationRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void execute(String email, String name, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(password);
        User user = new User(email, name, passwordHash);
        userRepository.save(user);

        String verificationCode = generateVerificationCode();
        EmailVerification emailVerification = new EmailVerification(email, verificationCode);
        emailVerificationRepository.save(emailVerification);

        emailService.sendVerificationEmail(email, verificationCode);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}