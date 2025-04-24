package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponInfo issueCoupon(CouponCommand command) {
        Coupon coupon = couponRepository.findByIdForUpdate(command.couponId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_COUPON));

        Coupon issuedCoupon = coupon.issue();
        couponRepository.save(issuedCoupon);

        return CouponInfo.from(issuedCoupon);
    }
}
