package team2.stk.shared.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Auth
    EMAIL_ALREADY_EXISTS("AUTH_001", "이미 사용 중인 이메일입니다."),
    INVALID_VERIFICATION_CODE("AUTH_002", "인증 코드가 올바르지 않거나 만료되었습니다."),
    EMAIL_NOT_VERIFIED("AUTH_003", "이메일 인증이 완료되지 않았습니다."),
    INVALID_CREDENTIALS("AUTH_004", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN("AUTH_005", "유효하지 않은 토큰입니다."),
    EXPIRED_REFRESH_TOKEN("AUTH_006", "Refresh Token이 만료되었습니다."),

    // Item
    ITEM_NOT_FOUND("ITEM_001", "자재를 찾을 수 없습니다."),
    DUPLICATE_ITEM_CODE("ITEM_002", "이미 존재하는 자재코드입니다."),

    // Movement
    INSUFFICIENT_STOCK("MOVEMENT_001", "재고가 부족합니다."),

    // Closing
    CLOSING_ORDER_VIOLATED("CLOSING_001", "이전 월 마감이 완료되지 않았습니다."),
    CLOSING_NOT_FOUND("CLOSING_002", "마감 내역을 찾을 수 없습니다."),

    // Common
    INVALID_INPUT("COMMON_001", "입력값이 올바르지 않습니다."),
    ACCESS_DENIED("COMMON_002", "접근이 거부되었습니다."),
    INTERNAL_SERVER_ERROR("COMMON_003", "서버 오류가 발생했습니다.");

    private final String code;
    private final String message;
}