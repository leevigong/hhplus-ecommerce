package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    private final UserCouponRepository userCouponRepository;

    public CouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional
    public List<UserCouponInfo> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> UserCouponInfo.from(userCoupon))
                .collect(Collectors.toList());
    }
}
