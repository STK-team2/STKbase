package team2.stk.domain.movement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import team2.stk.domain.item.Item;
import team2.stk.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMovement {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;

    private String reference;

    private String note;

    @Column(name = "exchange_ref")
    private UUID exchangeRef;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public StockMovement(Item item, User user, String site, MovementType type, int quantity,
                        LocalDate movementDate, String reference, String note) {
        this.item = item;
        this.user = user;
        this.site = site;
        this.type = type;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.reference = reference;
        this.note = note;
    }

    public StockMovement(Item item, User user, String site, MovementType type, int quantity,
                        LocalDate movementDate, String reference, String note, UUID exchangeRef) {
        this.item = item;
        this.user = user;
        this.site = site;
        this.type = type;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.reference = reference;
        this.note = note;
        this.exchangeRef = exchangeRef;
    }

    public void update(String site, int quantity, LocalDate movementDate, String reference, String note) {
        this.site = site;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.reference = reference;
        this.note = note;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}