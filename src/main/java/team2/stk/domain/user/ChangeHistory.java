package team2.stk.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team2.stk.shared.config.JsonbConverter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "change_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "screen_name", nullable = false)
    private String screenName;

    @Column(name = "record_id", nullable = false)
    private UUID recordId;

    @Column(nullable = false)
    private String action;

    @Column(name = "before_value", columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter.class)
    private Map<String, Object> beforeValue;

    @Column(name = "after_value", columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter.class)
    private Map<String, Object> afterValue;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public ChangeHistory(User user, String tableName, String screenName, UUID recordId, String action,
                        Map<String, Object> beforeValue, Map<String, Object> afterValue) {
        this.user = user;
        this.tableName = tableName;
        this.screenName = screenName;
        this.recordId = recordId;
        this.action = action;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
        this.changedAt = LocalDateTime.now();
    }
}