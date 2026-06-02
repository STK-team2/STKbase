package team2.stk.presentation.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team2.stk.domain.item.Category;
import team2.stk.infrastructure.persistence.item.CategoryRepository;
import team2.stk.shared.response.ApiResponse;

import java.util.*;

@Tag(name = "카테고리", description = "카테고리 관리 API")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    /**
     * 대분류 트리 형태로 반환
     * [{ id, name, children: [{ id, name }] }]
     */
    @Operation(summary = "카테고리 트리 조회 (대분류 > 중분류)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
        List<Category> topLevel = categoryRepository.findAllTopLevel();
        List<Map<String, Object>> result = topLevel.stream()
                .map(parent -> {
                    List<Map<String, Object>> children = categoryRepository.findAllByParentId(parent.getId())
                            .stream()
                            .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
                            .toList();
                    Map<String, Object> node = new LinkedHashMap<>();
                    node.put("id", parent.getId());
                    node.put("name", parent.getName());
                    node.put("children", children);
                    return node;
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * parentId 없으면 대분류, 있으면 중분류로 등록
     */
    @Operation(summary = "카테고리 등록 (관리자)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCategory(
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("INVALID_INPUT", "카테고리 이름은 필수입니다."));
        }

        Category parent = null;
        String parentIdStr = body.get("parentId");
        if (parentIdStr != null && !parentIdStr.isBlank()) {
            parent = categoryRepository.findById(UUID.fromString(parentIdStr)).orElse(null);
        }

        Category saved = categoryRepository.save(new Category(name, parent));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("parentId", saved.getParent() != null ? saved.getParent().getId() : null);
        response.put("isTopLevel", saved.isTopLevel());
        return ResponseEntity.ok(ApiResponse.success(response));
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
