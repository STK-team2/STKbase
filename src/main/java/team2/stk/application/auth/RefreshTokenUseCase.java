package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.RefreshToken;
import team2.stk.domain.user.exception.ExpiredRefreshTokenException;
import team2.stk.infrastructure.persistence.user.RefreshTokenRepository;
import team2.stk.shared.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public String execute(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(ExpiredRefreshTokenException::new);

        if (refreshToken.isExpired()) {
            throw new ExpiredRefreshTokenException();
        }

        return jwtProvider.generateAccessToken(refreshToken.getUser());
    }
}