package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon getById(Long Id);

    Optional<Coupon> findByIdForUpdate(Long Id);
}
