package kr.hhplus.be.server.domain.userCoupon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional(readOnly = true)
    public List<UserCouponInfo> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> UserCouponInfo.from(userCoupon))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createUserCoupon(UserCouponCommand command) {
        UserCoupon userCoupon = UserCoupon.create(command.coupon(), command.userId());
        userCouponRepository.save(userCoupon);
    }
}
