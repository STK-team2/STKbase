package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.RefreshToken;
import team2.stk.domain.user.User;
import team2.stk.domain.user.exception.ExpiredRefreshTokenException;
import team2.stk.infrastructure.persistence.user.RefreshTokenRepository;
import team2.stk.shared.jwt.JwtProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public RefreshResult execute(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(ExpiredRefreshTokenException::new);

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException();
        }

        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshTokenValue = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = new RefreshToken(user, newRefreshTokenValue);
        refreshTokenRepository.save(newRefreshToken);

        return new RefreshResult(newAccessToken, newRefreshTokenValue);
    }

    public record RefreshResult(String accessToken, String refreshToken) {}
}