package team2.stk.domain.user.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException() {
        super("Refresh Token이 만료되었습니다.");
    }
}