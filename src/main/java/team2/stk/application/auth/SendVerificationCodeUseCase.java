package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.exception.EmailAlreadyExistsException;
import team2.stk.infrastructure.mail.EmailService;
import team2.stk.infrastructure.persistence.user.UserRepository;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SendVerificationCodeUseCase {

    private static final String CODE_KEY_PREFIX = "auth:email:code:";
    private static final Duration CODE_TTL = Duration.ofMinutes(3);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional(readOnly = true)
    public void execute(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String verificationCode = generateVerificationCode();
        String key = CODE_KEY_PREFIX + email;
        stringRedisTemplate.opsForValue().set(key, verificationCode, CODE_TTL);
        emailService.sendVerificationEmail(email, verificationCode);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
