package kr.hhplus.be.server.infra.userCoupon;

import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserId(Long userId);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);

    boolean existsByCouponIdAndUserId(Long couponId, Long userId);
}
