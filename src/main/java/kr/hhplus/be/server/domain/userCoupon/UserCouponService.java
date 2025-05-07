package kr.hhplus.be.server.domain.userCoupon;

import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    public List<UserCouponInfo> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> UserCouponInfo.from(userCoupon))
                .collect(Collectors.toList());
    }

    public void createUserCoupon(UserCouponCommand command) {
        userCouponRepository.findByCouponIdAndUserId(command.coupon().getId(), command.userId())
                .ifPresent(uc -> {
                    throw new ApiException(ApiErrorCode.ALREADY_USER_COUPON);
                });

        UserCoupon userCoupon = UserCoupon.create(command.coupon(), command.userId());
        userCouponRepository.save(userCoupon);
    }
}
