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

    private final PushService pushService;
    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebPushService(
            @Value("${app.vapid.public-key}") String publicKey,
            @Value("${app.vapid.private-key}") String privateKey,
            @Value("${app.vapid.subject}") String subject,
            PushSubscriptionRepository subscriptionRepository) throws Exception {
        this.pushService = new PushService(publicKey, privateKey, subject);
        this.subscriptionRepository = subscriptionRepository;
    }

    public void notifyAdmins(String title, String body) {
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
