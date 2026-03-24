package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.RefreshToken;
import team2.stk.infrastructure.persistence.user.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class SignOutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void execute(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElse(null);

        if (refreshToken != null) {
            refreshTokenRepository.deleteByUser(refreshToken.getUser());
        }
    }
}