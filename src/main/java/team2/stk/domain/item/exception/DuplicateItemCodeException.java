package team2.stk.domain.item.exception;

public class DuplicateItemCodeException extends RuntimeException {
    public DuplicateItemCodeException(String itemCode) {
        super("이미 존재하는 자재코드입니다: " + itemCode);
    }
}