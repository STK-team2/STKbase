package team2.stk.domain.closing.exception;

public class ClosingOrderViolatedException extends RuntimeException {
    public ClosingOrderViolatedException(String closingYm) {
        super("이전 월 마감이 완료되지 않았습니다. 마감 월: " + closingYm);
    }
}