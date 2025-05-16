package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon getById(Long Id);

    Optional<Coupon> findByIdForUpdate(Long Id);

    List<Coupon> findByCouponStatus(CouponStatus couponStatus);
}
