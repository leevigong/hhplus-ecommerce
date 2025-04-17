package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "`order`")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", foreignKey = @ForeignKey(name = "fk_order_user_coupon"), unique = true)
    private UserCoupon userCoupon;

    private long totalPrice;

    private long discountAmount; // 쿠폰 사용시 할인

    private long finalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order createOrder(Long userId, List<OrderItem> items) {
        Order order = new Order();
        order.userId = userId;
        order.orderStatus = OrderStatus.PENDING;

        items.forEach(order::addItem);
        order.calculateTotalPrice();

        order.finalPrice = order.totalPrice;
        return order;
    }

    public void addItem(OrderItem item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }

    private void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .mapToLong(item -> item.getTotalPrice())
                .sum();
    }

    public void applyCoupon(UserCoupon userCoupon) {
        if (userCoupon == null) {
            throw new ApiException(ApiErrorCode.NOT_FOUND_USER_COUPON);
        }
        if (this.userCoupon != null) { // 주문에 이미 쿠폰이 붙어 있으면
            throw new ApiException(ApiErrorCode.ALREADY_COUPON_APPLIED);
        }

        userCoupon.validateAvailable();

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
