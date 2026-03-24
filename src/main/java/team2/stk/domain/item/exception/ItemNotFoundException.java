package team2.stk.domain.item.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String id) {
        super("자재를 찾을 수 없습니다: " + id);
    }
}