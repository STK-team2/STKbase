package team2.stk.presentation.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.item.DeleteItemUseCase;
import team2.stk.application.item.GetItemsUseCase;
import team2.stk.application.item.RegisterItemUseCase;
import team2.stk.domain.item.Item;
import team2.stk.presentation.item.dto.ItemResponse;
import team2.stk.presentation.item.dto.RegisterItemRequest;
import team2.stk.shared.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "자재", description = "자재 관리 API")
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final RegisterItemUseCase registerItemUseCase;
    private final DeleteItemUseCase deleteItemUseCase;
    private final GetItemsUseCase getItemsUseCase;

    @Operation(summary = "자재 등록", description = "새로운 자재를 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemResponse>> registerItem(@Valid @RequestBody RegisterItemRequest request) {
        Item item = registerItemUseCase.execute(
                request.getItemCode(),
                request.getItemName(),
                request.getBoxNumber(),
                request.getLocation(),
                request.getCategoryId(),
                request.getLowStockThreshold()
        );
        ItemResponse response = ItemResponse.from(item);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "자재 검색", description = "자재코드, 자재명, 카테고리로 자재를 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> searchItems(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) UUID categoryId) {
        List<ItemResponse> responses = getItemsUseCase.execute(query, categoryId).stream()
                .map(ItemResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "자재 삭제 (관리자)", description = "자재를 삭제합니다. (Soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable UUID id) {
        deleteItemUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
