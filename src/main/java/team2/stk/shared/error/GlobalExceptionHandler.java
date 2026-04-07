package team2.stk.shared.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import team2.stk.shared.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_INPUT.getCode(), message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("Binding error: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_INPUT.getCode(), message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch error: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_INPUT));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(ErrorCode.ACCESS_DENIED));
    }

    @ExceptionHandler(team2.stk.domain.user.exception.EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(team2.stk.domain.user.exception.EmailAlreadyExistsException e) {
        log.warn("Email already exists: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.EMAIL_ALREADY_EXISTS));
    }

    @ExceptionHandler(team2.stk.domain.user.exception.InvalidVerificationCodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidVerificationCode(team2.stk.domain.user.exception.InvalidVerificationCodeException e) {
        log.warn("Invalid verification code: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_VERIFICATION_CODE));
    }

    @ExceptionHandler(team2.stk.domain.user.exception.EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailNotVerified(team2.stk.domain.user.exception.EmailNotVerifiedException e) {
        log.warn("Email not verified: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.EMAIL_NOT_VERIFIED));
    }

    @ExceptionHandler(team2.stk.domain.user.exception.InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(team2.stk.domain.user.exception.InvalidCredentialsException e) {
        log.warn("Invalid credentials: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(ErrorCode.INVALID_CREDENTIALS));
    }

    @ExceptionHandler(team2.stk.domain.user.exception.ExpiredRefreshTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredRefreshToken(team2.stk.domain.user.exception.ExpiredRefreshTokenException e) {
        log.warn("Expired refresh token: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(ErrorCode.EXPIRED_REFRESH_TOKEN));
    }

    @ExceptionHandler(team2.stk.domain.item.exception.ItemNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleItemNotFound(team2.stk.domain.item.exception.ItemNotFoundException e) {
        log.warn("Item not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ErrorCode.ITEM_NOT_FOUND));
    }

    @ExceptionHandler(team2.stk.domain.item.exception.DuplicateItemCodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateItemCode(team2.stk.domain.item.exception.DuplicateItemCodeException e) {
        log.warn("Duplicate item code: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.DUPLICATE_ITEM_CODE));
    }

    @ExceptionHandler(team2.stk.domain.movement.exception.MovementNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMovementNotFound(team2.stk.domain.movement.exception.MovementNotFoundException e) {
        log.warn("Movement not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ErrorCode.MOVEMENT_NOT_FOUND));
    }

    @ExceptionHandler(team2.stk.domain.movement.exception.InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(team2.stk.domain.movement.exception.InsufficientStockException e) {
        log.warn("Insufficient stock: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INSUFFICIENT_STOCK.getCode(), e.getMessage()));
    }

    @ExceptionHandler(team2.stk.domain.closing.exception.ClosingOrderViolatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleClosingOrderViolated(team2.stk.domain.closing.exception.ClosingOrderViolatedException e) {
        log.warn("Closing order violated: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.CLOSING_ORDER_VIOLATED));
    }

    @ExceptionHandler(team2.stk.domain.closing.exception.ClosingNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleClosingNotFound(team2.stk.domain.closing.exception.ClosingNotFoundException e) {
        log.warn("Closing not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ErrorCode.CLOSING_NOT_FOUND));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}