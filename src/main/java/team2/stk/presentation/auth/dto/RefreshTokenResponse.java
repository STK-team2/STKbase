package team2.stk.presentation.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResponse {
    private final String accessToken;
    private final String refreshToken;
}