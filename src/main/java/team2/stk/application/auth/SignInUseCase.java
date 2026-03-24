package team2.stk.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.user.RefreshToken;
import team2.stk.domain.user.User;
import team2.stk.domain.user.exception.EmailNotVerifiedException;
import team2.stk.domain.user.exception.InvalidCredentialsException;
import team2.stk.infrastructure.persistence.user.RefreshTokenRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.jwt.JwtProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignInUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignInResult execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(user, refreshTokenValue);
        refreshTokenRepository.save(refreshToken);

        return new SignInResult(accessToken, refreshTokenValue);
    }

    public record SignInResult(String accessToken, String refreshToken) {}
}