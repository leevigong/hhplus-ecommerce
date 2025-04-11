package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    private UserCoupon userCoupon;

    private long totalPrice;

    private long discountAmount; // 쿠폰 사용시 할인

    private long finalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public static Order createOrder(Long userId, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .userId(userId)
                .orderItems(orderItems)
                .orderStatus(OrderStatus.PENDING)
                .build();
        order.calculateTotalPrice();
        return order;
    }

    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream().mapToLong(OrderItem::getTotalPrice).sum();
    }

    public void applyCoupon(UserCoupon userCoupon) {
        if (this.userCoupon != null) {
            throw new ApiException(ApiErrorCode.ALREADY_COUPON_APPLIED);
        }
        if (userCoupon == null || !userCoupon.isUsable()) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        userCoupon.use();

        long discount = userCoupon.getCoupon().calculateDiscount(this.totalPrice);
        this.discountAmount = Math.min(discount, this.totalPrice);
        this.finalPrice = this.totalPrice - this.discountAmount;

        this.userCoupon = userCoupon;
    }

    public void cancelCoupon() {
        if (this.userCoupon == null) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        this.userCoupon.cancel();
        this.userCoupon = null;
        this.discountAmount = 0;
        this.finalPrice = this.totalPrice;
    }

    public void confirmOrder() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new ApiException(ApiErrorCode.INVALID_ORDER_STATUS);
        }

        this.orderStatus = OrderStatus.PAID;
    }

    public void cancelOrder() {
        if (this.orderStatus == OrderStatus.PENDING || this.orderStatus == OrderStatus.CANCELED) {
            throw new ApiException(ApiErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.CANCELED;
        if (this.userCoupon != null) {
            this.userCoupon.cancel();
            this.userCoupon = null;
        }
    }
}
