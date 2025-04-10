package kr.hhplus.be.server.domain.coupon;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findById(Long couponId);

}
