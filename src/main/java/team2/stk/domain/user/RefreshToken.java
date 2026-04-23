package team2.stk.domain.user;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshToken {

    private final User user;
    private final String token;
    private final LocalDateTime expiresAt;

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiresAt = LocalDateTime.now().plusDays(14);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
