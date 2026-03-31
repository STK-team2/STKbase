package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.User;
import team2.stk.domain.user.exception.EmailAlreadyExistsException;
import team2.stk.domain.user.exception.EmailNotVerifiedException;
import team2.stk.infrastructure.persistence.user.UserRepository;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {

    private static final String VERIFIED_KEY_PREFIX = "auth:email:verified:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public void execute(String email, String name, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String verifiedKey = VERIFIED_KEY_PREFIX + email;
        String verifiedValue = stringRedisTemplate.opsForValue().get(verifiedKey);
        if (!"true".equals(verifiedValue)) {
            throw new EmailNotVerifiedException();
        }

        String passwordHash = passwordEncoder.encode(password);
        User user = new User(email, name, passwordHash);
        user.verify();
        userRepository.save(user);
        stringRedisTemplate.delete(verifiedKey);
    }
}
