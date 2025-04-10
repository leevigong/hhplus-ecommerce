package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.User;
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
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Coupon coupon;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus couponStatus;


    public void use() {
        if (!isUsable()) {
            throw new ApiException(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE);
        }
        this.couponStatus = UserCouponStatus.USED;
    }

    public void cancel() {
        if (this.couponStatus != UserCouponStatus.USED) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        if (coupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ApiErrorCode.COUPON_EXPIRED);
        }

        this.couponStatus = UserCouponStatus.AVAILABLE;
    }


    public boolean isUsable() {
        return this.couponStatus == UserCouponStatus.AVAILABLE &&
                coupon.getExpiredAt().isAfter(LocalDateTime.now());
    }
}
