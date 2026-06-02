package team2.stk.infrastructure.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team2.stk.domain.push.PushSubscription;
import team2.stk.infrastructure.persistence.push.PushSubscriptionRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebPushService {

    private PushService pushService;
    private final boolean enabled;
    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebPushService(
            @Value("${app.vapid.public-key:}") String publicKey,
            @Value("${app.vapid.private-key:}") String privateKey,
            @Value("${app.vapid.subject:}") String subject,
            PushSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;

        if (publicKey.isBlank() || privateKey.isBlank()) {
            log.warn("VAPID 키가 설정되지 않아 Web Push 알림이 비활성화됩니다.");
            this.enabled = false;
            return;
        }

        boolean init = false;
        try {
            this.pushService = new PushService(publicKey, privateKey, subject);
            init = true;
        } catch (Exception e) {
            log.warn("Web Push 초기화 실패 - 알림 비활성화: {}", e.getMessage());
        }
        this.enabled = init;
    }

    public void notifyAdmins(String title, String body) {
        if (!enabled) {
            log.debug("Web Push 비활성화 상태 - 알림 스킵: {}", title);
            return;
        }
        List<PushSubscription> admins = subscriptionRepository.findAllAdmins();
        for (PushSubscription sub : admins) {
            try {
                String payload = objectMapper.writeValueAsString(Map.of("title", title, "body", body));
                Subscription subscription = new Subscription(
                        sub.getEndpoint(),
                        new Subscription.Keys(sub.getP256dh(), sub.getAuth())
                );
                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
            } catch (Exception e) {
                log.warn("푸시 알림 발송 실패 - endpoint: {}, error: {}", sub.getEndpoint(), e.getMessage());
            }
        }
    }
}
