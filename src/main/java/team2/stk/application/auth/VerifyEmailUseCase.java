package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.exception.InvalidVerificationCodeException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCase {

    private static final String CODE_KEY_PREFIX = "auth:email:code:";
    private static final String VERIFIED_KEY_PREFIX = "auth:email:verified:";
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public void execute(String email, String code) {
        String codeKey = CODE_KEY_PREFIX + email;
        String savedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        String verifiedKey = VERIFIED_KEY_PREFIX + email;
        stringRedisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_TTL);
        stringRedisTemplate.delete(codeKey);
    }
}
