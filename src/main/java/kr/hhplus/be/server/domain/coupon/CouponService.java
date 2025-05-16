package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public void updateIssuedCount(CouponCommand.Publish command) {
        command.getCoupon().updateIssuedQuantity(command.getLimit());
    }

    public List<CouponInfo.PublishableCoupon> getPublishableCoupons() {
        return couponRepository.findByCouponStatus(CouponStatus.ACTIVE).stream()
                .map(coupon -> CouponInfo.PublishableCoupon.of(coupon, coupon.getPublishableQuantity()))
                .collect(Collectors.toList());
    }
}
