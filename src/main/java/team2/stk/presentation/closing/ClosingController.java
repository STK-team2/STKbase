package team2.stk.presentation.closing;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.closing.CancelClosingUseCase;
import team2.stk.application.closing.CloseMonthUseCase;
import team2.stk.application.closing.GetClosingStockUseCase;
import team2.stk.domain.closing.ClosingStatus;
import team2.stk.presentation.closing.dto.CloseMonthRequest;
import team2.stk.presentation.closing.dto.CloseMonthResponse;
import team2.stk.presentation.closing.dto.ClosingStockResponse;
import team2.stk.shared.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "마감처리", description = "마감 처리 API")
@RestController
@RequestMapping("/closing")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ClosingController {

    private final CloseMonthUseCase closeMonthUseCase;
    private final CancelClosingUseCase cancelClosingUseCase;
    private final GetClosingStockUseCase getClosingStockUseCase;

    @Operation(summary = "마감 처리", description = "지정된 월의 재고를 마감 처리합니다. 순서대로 마감해야 합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<List<CloseMonthResponse>>> closeMonth(@Valid @RequestBody CloseMonthRequest request) {
        List<CloseMonthUseCase.CloseResult> results = closeMonthUseCase.execute(request.getClosingYm());
        List<CloseMonthResponse> responses = results.stream()
                .map(CloseMonthResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "마감 취소", description = "마감 처리를 취소합니다.")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelClosing(@PathVariable UUID id) {
        cancelClosingUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "마감 재고 조회", description = "마감된 재고 현황을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClosingStockResponse>>> getClosingStock(
            @RequestParam(required = false) String closingYm,
            @RequestParam(required = false) ClosingStatus status) {

        List<GetClosingStockUseCase.ClosingStockResult> results = getClosingStockUseCase.execute(closingYm, status);
        List<ClosingStockResponse> responses = results.stream()
                .map(ClosingStockResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}