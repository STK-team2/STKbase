package team2.stk.domain.movement.exception;

public class MovementNotFoundException extends RuntimeException {
    public MovementNotFoundException(String id) {
        super("입출고 내역을 찾을 수 없습니다: " + id);
    }
}