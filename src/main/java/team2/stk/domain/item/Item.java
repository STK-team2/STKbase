package team2.stk.domain.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "item_code", nullable = false, unique = true)
    private String itemCode;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "box_number")
    private String boxNumber;

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Column(name = "image_url")
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Item(String itemCode, String itemName, String boxNumber, String location,
                Category category, Integer lowStockThreshold) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.boxNumber = boxNumber;
        this.location = location;
        this.category = category;
        this.lowStockThreshold = lowStockThreshold;
    }

    public void update(String itemName, String boxNumber, String location, Category category) {
        this.itemName = itemName;
        this.boxNumber = boxNumber;
        this.location = location;
        this.category = category;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateLowStockThreshold(Integer threshold) {
        this.lowStockThreshold = threshold;
    }

    public boolean isLowStock(int currentStock) {
        return lowStockThreshold != null && currentStock <= lowStockThreshold;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}