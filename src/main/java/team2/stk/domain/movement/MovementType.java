package team2.stk.domain.movement;

public enum MovementType {
    INBOUND,          // 입고 → 재고 증가
    OUTBOUND,         // 출고 → 재고 감소
    RETURN_INBOUND,   // 입고 반품 → 재고 감소 (공급업체에 반품)
    RETURN_OUTBOUND,  // 출고 반품 → 재고 증가 (현장→창고 반납)
    EXCHANGE_OUT,     // 교환 출고 → 재고 감소 (A자재 나감)
    EXCHANGE_IN       // 교환 입고 → 재고 증가 (B자재 들어옴)
}