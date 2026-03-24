package team2.stk.domain.movement.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String itemCode, int currentStock, int requestedQuantity) {
        super(String.format("재고가 부족합니다. 자재코드: %s, 현재재고: %d, 요청수량: %d",
                itemCode, currentStock, requestedQuantity));
    }
}