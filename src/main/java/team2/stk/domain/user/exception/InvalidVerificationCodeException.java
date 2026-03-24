package team2.stk.domain.user.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("인증 코드가 올바르지 않거나 만료되었습니다.");
    }
}