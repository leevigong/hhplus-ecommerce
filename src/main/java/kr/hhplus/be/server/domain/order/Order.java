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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public static Order createOrder(Long userId, List<OrderItem> orderItems) {
        return Order.builder()
                .userId(userId)
                .orderItems(orderItems)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    public void calculateTotalPrice(List<OrderItem> orderItems) {
        this.totalPrice = orderItems.stream()
                .mapToLong(item -> item.getTotalPrice())
                .sum();
    }

    public void applyCoupon(UserCoupon userCoupon) {
        if (this.userCoupon != null) {
            throw new ApiException(ApiErrorCode.ALREADY_COUPON_APPLIED);
        }
        if (userCoupon == null || !userCoupon.isUsable()) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        // 최종 가격 계산 메서드 호출 (내부에서 쿠폰 사용 및 할인 적용)
        this.finalPrice = calculateFinalPrice(userCoupon, this.totalPrice);
        this.userCoupon = userCoupon;
    }

    private long calculateFinalPrice(UserCoupon userCoupon, long totalPrice) {
        userCoupon.use();

        long discount = userCoupon.getCoupon().calculateDiscount(totalPrice);
        this.discountAmount = discount;

        return totalPrice - discount;
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
