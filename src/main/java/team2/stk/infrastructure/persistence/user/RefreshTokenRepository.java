package team2.stk.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import team2.stk.domain.user.RefreshToken;
import team2.stk.domain.user.User;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    private static final String TOKEN_PREFIX = "refresh:token:";
    private static final String USER_PREFIX  = "refresh:user:";
    private static final Duration TTL = Duration.ofDays(14);

    public Optional<RefreshToken> findByToken(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (userId == null) return Optional.empty();
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> new RefreshToken(user, token));
    }

    public RefreshToken save(RefreshToken refreshToken) {
        String token  = refreshToken.getToken();
        String userId = refreshToken.getUser().getId().toString();
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token,  userId, TTL);
        redisTemplate.opsForValue().set(USER_PREFIX  + userId, token,  TTL);
        return refreshToken;
    }

    public void delete(RefreshToken refreshToken) {
        String token  = refreshToken.getToken();
        String userId = refreshToken.getUser().getId().toString();
        redisTemplate.delete(TOKEN_PREFIX + token);
        redisTemplate.delete(USER_PREFIX  + userId);
    }

    public void deleteByUser(User user) {
        String userId = user.getId().toString();
        String token  = redisTemplate.opsForValue().get(USER_PREFIX + userId);
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
        redisTemplate.delete(USER_PREFIX + userId);
    }
}
