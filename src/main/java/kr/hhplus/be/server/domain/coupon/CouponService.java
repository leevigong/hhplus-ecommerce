package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponInfo.Issue issueCoupon(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.getById(command.getCouponId());

        Coupon issuedCoupon = coupon.issue();
        couponRepository.save(issuedCoupon);

        return CouponInfo.Issue.from(issuedCoupon);
    }
}
