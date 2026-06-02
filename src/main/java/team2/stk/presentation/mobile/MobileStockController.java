package team2.stk.presentation.mobile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.movement.RegisterInboundUseCase;
import team2.stk.application.movement.RegisterOutboundUseCase;
import team2.stk.domain.item.Item;
import team2.stk.domain.movement.StockMovement;
import team2.stk.infrastructure.persistence.item.ItemRepository;
import team2.stk.infrastructure.persistence.movement.StockMovementRepository;
import team2.stk.shared.response.ApiResponse;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Tag(name = "모바일", description = "모바일 전용 간단 재고 API")
@RestController
@RequestMapping("/mobile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MobileStockController {

    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final RegisterInboundUseCase registerInboundUseCase;
    private final RegisterOutboundUseCase registerOutboundUseCase;

    @Operation(summary = "재고 확인", description = "자재코드로 현재 재고 수량 조회")
    @GetMapping("/stock/{itemId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStock(@PathVariable UUID itemId) {
        Item item = itemRepository.findByIdActive(itemId)
                .orElseThrow(() -> new IllegalArgumentException("자재를 찾을 수 없습니다."));
        int stock = stockMovementRepository.calculateCurrentStock(itemId);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "itemId", item.getId(),
                "itemCode", item.getItemCode(),
                "itemName", item.getItemName(),
                "location", item.getLocation() != null ? item.getLocation() : "",
                "imageUrl", item.getImageUrl() != null ? item.getImageUrl() : "",
                "currentStock", stock
        )));
    }

    @Operation(summary = "수량 증가 (입고)", description = "자재 수량을 증가시킵니다.")
    @PostMapping("/stock/{itemId}/increase")
    public ResponseEntity<ApiResponse<Map<String, Object>>> increase(
            @PathVariable UUID itemId,
            @Valid @RequestBody AdjustRequest request) {

        StockMovement movement = registerInboundUseCase.execute(
                request.getSite(), itemId, request.getQuantity(),
                LocalDate.now(), null, request.getNote());

        int newStock = stockMovementRepository.calculateCurrentStock(itemId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "quantity", request.getQuantity(),
                "currentStock", newStock
        )));
    }

    @Operation(summary = "수량 차감 (출고)", description = "자재 수량을 차감시킵니다.")
    @PostMapping("/stock/{itemId}/decrease")
    public ResponseEntity<ApiResponse<Map<String, Object>>> decrease(
            @PathVariable UUID itemId,
            @Valid @RequestBody AdjustRequest request) {

        StockMovement movement = registerOutboundUseCase.execute(
                request.getSite(), itemId, request.getQuantity(),
                LocalDate.now(), null, request.getNote(), false);

        int newStock = stockMovementRepository.calculateCurrentStock(itemId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "quantity", request.getQuantity(),
                "currentStock", newStock
        )));
    }

    @Getter
    @NoArgsConstructor
    public static class AdjustRequest {
        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private Integer quantity;
        private String site;
        private String note;
    }
}
