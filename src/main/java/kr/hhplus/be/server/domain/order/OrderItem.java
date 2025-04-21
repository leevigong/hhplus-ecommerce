package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "order_item")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    private Long productId;

    private int quantity;

    private long price;

    public static OrderItem create(Long productId, int quantity, long price) {
        return OrderItem.builder()
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public long getTotalPrice() {
        return price * quantity;
    }
}
