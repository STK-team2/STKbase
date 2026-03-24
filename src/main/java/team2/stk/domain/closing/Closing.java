package team2.stk.domain.closing;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team2.stk.domain.item.Item;
import team2.stk.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "closing")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Closing {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "item_code", nullable = false)
    private String itemCode;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "closing_ym", nullable = false)
    private String closingYm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClosingStatus status;

    @Column(name = "opening_stock", nullable = false)
    private int openingStock;

    @Column(name = "inbound_qty", nullable = false)
    private int inboundQty;

    @Column(name = "outbound_qty", nullable = false)
    private int outboundQty;

    @Column(name = "closing_stock", nullable = false)
    private int closingStock;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    public Closing(Item item, User user, String closingYm, int openingStock,
                  int inboundQty, int outboundQty) {
        this.item = item;
        this.user = user;
        this.itemCode = item.getItemCode();
        this.itemName = item.getItemName();
        this.closingYm = closingYm;
        this.status = ClosingStatus.CLOSED;
        this.openingStock = openingStock;
        this.inboundQty = inboundQty;
        this.outboundQty = outboundQty;
        this.closingStock = openingStock + inboundQty - outboundQty;
        this.closedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ClosingStatus.CANCELLED;
    }

    public void reopen() {
        this.status = ClosingStatus.CLOSED;
    }
}