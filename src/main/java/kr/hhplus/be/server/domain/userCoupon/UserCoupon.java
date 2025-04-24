package kr.hhplus.be.server.domain.userCoupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.Coupon;
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
@Table(name = "user_coupon")
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", foreignKey = @ForeignKey(name = "fk_user_coupon_coupon"))
    private Coupon coupon;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus userCouponStatus;

    public static UserCoupon create(Coupon coupon, Long userId, UserCouponStatus userCouponStatus) {
        return UserCoupon.builder()
                .coupon(coupon)
                .userId(userId)
                .userCouponStatus(userCouponStatus)
                .build();
    }

    public void use() {
        if (!isUsable()) {
            throw new ApiException(ApiErrorCode.USER_COUPON_EXPIRED);
        }
        this.userCouponStatus = UserCouponStatus.USED;
    }

    public void cancel() {
        if (coupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ApiErrorCode.USER_COUPON_EXPIRED);
        }

        if (this.userCouponStatus != UserCouponStatus.USED) {
            throw new ApiException(ApiErrorCode.INVALID_COUPON_STATUS);
        }

        this.userCouponStatus = UserCouponStatus.AVAILABLE;
    }

    public boolean isUsable() {
        return this.userCouponStatus == UserCouponStatus.AVAILABLE &&
                coupon.getExpiredAt().isAfter(LocalDateTime.now());
    }

    public boolean isUsed() {
        return this.userCouponStatus == UserCouponStatus.USED;
    }

    public void validateAvailable() {
        if (isUsed()) {
            throw new ApiException(ApiErrorCode.ALREADY_COUPON_APPLIED);
        }
        if (!isUsable()) {
            throw new ApiException(ApiErrorCode.USER_COUPON_EXPIRED);
        }
    }
}
