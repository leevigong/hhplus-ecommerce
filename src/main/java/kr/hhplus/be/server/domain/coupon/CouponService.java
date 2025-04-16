package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;

    public CouponService(UserCouponRepository userCouponRepository,
                         CouponRepository couponRepository) {
        this.userCouponRepository = userCouponRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional(readOnly = true)
    public List<UserCouponInfo> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> UserCouponInfo.from(userCoupon))
                .collect(Collectors.toList());
    }

    public UserCouponInfo issueCoupon(UserCouponCommand command) {
        Coupon coupon = couponRepository.getById(command.couponId());

        coupon.issue();
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(coupon)
                .userId(command.userId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);
        return UserCouponInfo.from(savedUserCoupon);
    }
}
