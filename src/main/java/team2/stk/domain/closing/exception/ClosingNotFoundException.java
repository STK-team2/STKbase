package team2.stk.domain.closing.exception;

public class ClosingNotFoundException extends RuntimeException {
    public ClosingNotFoundException(String id) {
        super("마감 내역을 찾을 수 없습니다: " + id);
    }
}