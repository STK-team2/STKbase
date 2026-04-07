package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import team2.stk.domain.user.exception.InvalidVerificationCodeException;
import team2.stk.shared.util.RedisKeyConstants;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCase {

    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate stringRedisTemplate;

    public void execute(String email, String code) {
        String codeKey = RedisKeyConstants.EMAIL_CODE_PREFIX + email;
        String savedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        String verifiedKey = RedisKeyConstants.EMAIL_VERIFIED_PREFIX + email;
        stringRedisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_TTL);
        stringRedisTemplate.delete(codeKey);
    }
}
