package team2.stk.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team2.stk.domain.user.Role;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "사용자", description = "사용자 관리 API (관리자 전용)")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "전체 사용자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "name", u.getName(),
                        "role", u.getRole().name(),
                        "verified", u.isVerified()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "사용자 권한 변경")
    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> changeRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.changeRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
