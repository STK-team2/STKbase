package team2.stk.presentation.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.application.item.DeleteItemUseCase;
import team2.stk.application.item.RegisterItemUseCase;
import team2.stk.domain.item.Item;
import team2.stk.infrastructure.persistence.item.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Operation(summary = "자재 등록", description = "새로운 자재를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> registerItem(@Valid @RequestBody RegisterItemRequest request) {
        Item item = registerItemUseCase.execute(
                request.getItemCode(),
                request.getItemName(),
                request.getBoxNumber(),
                request.getLocation()
        );
        ItemResponse response = ItemResponse.from(item);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "자재 검색", description = "자재코드 또는 자재명으로 자재를 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> searchItems(@RequestParam(required = false, defaultValue = "") String query) {
        List<Item> items;
        if (query.trim().isEmpty()) {
            items = itemRepository.findAllActive();
        } else {
            items = itemRepository.searchActive(query.trim());
        }

        List<ItemResponse> responses = items.stream()
                .map(ItemResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "자재 삭제", description = "자재를 삭제합니다. (Soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable UUID id) {
        deleteItemUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}