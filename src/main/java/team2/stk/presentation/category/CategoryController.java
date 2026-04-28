package team2.stk.presentation.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team2.stk.domain.item.Category;
import team2.stk.infrastructure.persistence.item.CategoryRepository;
import team2.stk.shared.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "카테고리", description = "카테고리 관리 API")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Operation(summary = "카테고리 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
        List<Map<String, Object>> categories = categoryRepository.findAllActive().stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @Operation(summary = "카테고리 등록 (관리자)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCategory(
            @Valid @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("INVALID_INPUT", "카테고리 이름은 필수입니다."));
        }
        Category saved = categoryRepository.save(new Category(name));
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", saved.getId(), "name", saved.getName())));
    }

    @Operation(summary = "카테고리 삭제 (관리자)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryRepository.findById(id).ifPresent(c -> {
            c.delete();
            categoryRepository.save(c);
        });
        return ResponseEntity.ok(ApiResponse.success());
    }
}
