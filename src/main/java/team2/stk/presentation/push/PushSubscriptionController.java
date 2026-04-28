package team2.stk.presentation.push;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.stk.domain.push.PushSubscription;
import team2.stk.domain.user.User;
import team2.stk.infrastructure.persistence.push.PushSubscriptionRepository;
import team2.stk.infrastructure.persistence.user.UserRepository;
import team2.stk.shared.response.ApiResponse;
import team2.stk.shared.util.SecurityUtil;

import java.util.Map;
import java.util.UUID;

@Tag(name = "푸시 알림", description = "Web Push 구독 관리 API")
@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PushSubscriptionController {

    private final PushSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Operation(summary = "푸시 구독 등록")
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(@RequestBody Map<String, Object> body) {
        UUID userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        String endpoint = (String) body.get("endpoint");
        @SuppressWarnings("unchecked")
        Map<String, String> keys = (Map<String, String>) body.get("keys");
        String p256dh = keys.get("p256dh");
        String auth   = keys.get("auth");

        subscriptionRepository.save(new PushSubscription(user, endpoint, p256dh, auth));
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "푸시 구독 해제")
    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(@RequestBody Map<String, String> body) {
        subscriptionRepository.deleteByEndpoint(body.get("endpoint"));
        return ResponseEntity.ok(ApiResponse.success());
    }
}
