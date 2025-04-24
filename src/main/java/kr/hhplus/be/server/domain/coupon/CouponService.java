package kr.hhplus.be.server.domain.coupon;

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
        Coupon coupon = couponRepository.getById(command.couponId());

        Coupon issuedCoupon = coupon.issue();
        couponRepository.save(issuedCoupon);

        return CouponInfo.from(issuedCoupon);
    }
}
