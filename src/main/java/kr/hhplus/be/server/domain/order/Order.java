package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private long discountPrice;

    private long finalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    private Order(Long userId, List<OrderItem> orderItems) {
        validateUserId(userId);
        validateOrderItems(orderItems);

        this.userId = userId;
        this.orderStatus = OrderStatus.PENDING;
        this.orderItems = new ArrayList<>();

        orderItems.forEach(this::addItem);
        calculatePrice();
    }

    public static Order create(Long userId, List<OrderItem> items) {
        return Order.builder()
                .userId(userId)
                .orderItems(items)
                .build();
    }

    public static Order createWithCoupon(Long userId, List<OrderItem> items, UserCoupon coupon) {
        Order order = create(userId, items);
        order.applyCoupon(coupon);
        return order;
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
        calculatePrice();
    }

    public void applyCoupon(UserCoupon coupon) {
        if (this.userCoupon != null) {
            throw new ApiException(ApiErrorCode.ALREADY_COUPON_APPLIED);
        }
        coupon.validateAvailable();

        long discount = coupon.getCoupon().calculateDiscount(this.totalPrice);
        coupon.use();

        this.userCoupon = coupon;
        this.discountPrice = discount;
        this.finalPrice = this.totalPrice - discount;
    }

    public void cancelCoupon() {
        if (this.userCoupon == null) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        userCoupon.cancel();
        this.userCoupon = null;
        this.discountPrice = 0;
        this.finalPrice = this.totalPrice;
    }

    public void confirmOrder() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new ApiException(ApiErrorCode.INVALID_ORDER_STATUS);
        }

        this.orderStatus = OrderStatus.PAID;
    }

    public void cancelOrder() {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new ApiException(ApiErrorCode.INVALID_ORDER_STATUS);
        }

        this.orderStatus = OrderStatus.CANCELED;

        if (this.userCoupon != null) {
            userCoupon.cancel();
            this.userCoupon = null;
        }
    }

    private void calculatePrice() {
        this.totalPrice = orderItems.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();

        if (this.userCoupon != null) {
            long discount = userCoupon.getCoupon().calculateDiscount(totalPrice);
            this.discountPrice = discount;
            this.finalPrice = totalPrice - discount;
        } else {
            this.discountPrice = 0;
            this.finalPrice = totalPrice;
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ApiException(ApiErrorCode.INVALID_USER);
        }
    }

    private void validateOrderItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new ApiException(ApiErrorCode.EMPTY_ORDER_ITEMS);
        }
    }
}
