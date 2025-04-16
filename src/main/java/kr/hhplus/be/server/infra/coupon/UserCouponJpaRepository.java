package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserId(Long userId);

}
