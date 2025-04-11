package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public static Coupon create(String couponCode, DiscountType discountType, long discountAmount, int maxIssuedQuantity) {
        return Coupon.builder()
                .couponCode(couponCode)
                .discountType(discountType)
                .discountAmount(discountAmount)
                .maxIssuedQuantity(maxIssuedQuantity)
                .couponStatus(CouponStatus.ACTIVE)
                .build();
    }

    public boolean isAvailableToIssue() {
        return this.couponStatus == CouponStatus.ACTIVE &&
                this.expiredAt != null && this.expiredAt.isAfter(LocalDateTime.now()) &&
                this.issuedQuantity < this.maxIssuedQuantity;
    }

    public void issue() {
        if (!isAvailableToIssue()) {
            throw new ApiException(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE);
        }

        this.issuedQuantity += 1;

        if (this.issuedQuantity >= this.maxIssuedQuantity) {
            this.couponStatus = CouponStatus.SOLD_OUT;
        }
    }

    public void expire() {
        this.couponStatus = CouponStatus.EXPIRED;
    }

    public long calculateDiscount(long totalPrice) {
        if (this.discountType == DiscountType.FIXED) {
            return Math.min(this.discountAmount, totalPrice);
        } else if (this.discountType == DiscountType.PERCENTAGE) {
            return (totalPrice * this.discountAmount) / 100;
        }
        throw new ApiException(ApiErrorCode.INVALID_DISCOUNT_AMOUNT);
    }

}
