package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Coupon getById(Long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_COUPON));
    }
}
