package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.entity.BaseEntity;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponCode;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private long discountAmount;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    private int maxIssuedQuantity;

    private int issuedQuantity;

    private LocalDateTime expiredAt;

    public static Coupon create(String couponCode, DiscountType discountType, long discountAmount, int maxIssuedQuantity, CouponStatus couponStatus, LocalDateTime expiredAt) {
        return Coupon.builder()
                .couponCode(couponCode)
                .discountType(discountType)
                .discountAmount(discountAmount)
                .maxIssuedQuantity(maxIssuedQuantity)
                .issuedQuantity(0)
                .couponStatus(couponStatus)
                .expiredAt(expiredAt)
                .build();
    }

    public static Coupon createFixed(String couponCode, long discountAmount, int maxIssuedQuantity, LocalDateTime expiredAt) {
        return Coupon.builder()
                .couponCode(couponCode)
                .discountType(DiscountType.FIXED)
                .discountAmount(discountAmount)
                .maxIssuedQuantity(maxIssuedQuantity)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.ACTIVE)
                .expiredAt(expiredAt)
                .build();
    }

    public static Coupon createPercentage(String couponCode, long discountAmount, int maxIssuedQuantity, LocalDateTime expiredAt) {
        return Coupon.builder()
                .couponCode(couponCode)
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(discountAmount)
                .maxIssuedQuantity(maxIssuedQuantity)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.ACTIVE)
                .expiredAt(expiredAt)
                .build();
    }

    public boolean isAvailableToIssue() {
        return this.couponStatus == CouponStatus.ACTIVE &&
                this.expiredAt != null && this.expiredAt.isAfter(LocalDateTime.now()) &&
                this.issuedQuantity < this.maxIssuedQuantity;
    }

    public Coupon issue() {
        if (!isAvailableToIssue()) {
            throw new ApiException(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE);
        }

        this.issuedQuantity += 1;

        if (this.issuedQuantity >= this.maxIssuedQuantity) {
            this.couponStatus = CouponStatus.SOLD_OUT;
        }

        return this;
    }

    public void expire() {
        this.couponStatus = CouponStatus.EXPIRED;
    }

    public long calculateDiscount(long totalPrice) {
        long discount;

        if (this.discountType == DiscountType.FIXED) {
            discount = this.discountAmount;
        } else if (this.discountType == DiscountType.PERCENTAGE) {
            discount = (totalPrice * this.discountAmount) / 100;
        } else {
            throw new ApiException(ApiErrorCode.INVALID_DISCOUNT_AMOUNT);
        }

        return Math.min(discount, totalPrice);
    }

    public void soldOut() {
        this.couponStatus = CouponStatus.SOLD_OUT;
    }

    public void updateIssuedQuantity(int quantity) {
        this.issuedQuantity += quantity;
    }
}
