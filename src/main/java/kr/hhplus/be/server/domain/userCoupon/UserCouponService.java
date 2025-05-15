package kr.hhplus.be.server.domain.userCoupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public void requestPublishCoupon(CouponCommand.PublishRequest command) {
        boolean isSuccess = userCouponRepository.enqueueCouponCandidate(command.getCouponId(), command.getUserId());
        if (!isSuccess) {
            throw new ApiException(ApiErrorCode.ALREADY_USER_COUPON);
        }
    }

    public void publishCouponCandidate(CouponCommand.Publish command) {
        long couponId = command.getCoupon().getId();
        int limit = command.getLimit();

        Set<Long> candidates = userCouponRepository.fetchCouponCandidates(couponId, limit);
        if (candidates == null || candidates.isEmpty()) {
            return;
        }

        List<Long> issued = new ArrayList<>();

        for (Object raw : candidates) {
            long userId;
            if (raw instanceof Long l) {
                userId = l;
            } else if (raw instanceof Integer i) {
                userId = i.longValue();
            } else if (raw instanceof String s) {
                userId = Long.parseLong(s);
            } else {
                continue;
            }

            // 이미 발급된 사용자면 스킵
            boolean exists = userCouponRepository.existsByCouponIdAndUserId(couponId, userId);
            if (exists) continue;

            UserCoupon coupon = UserCoupon.create(command.getCoupon(), userId);
            userCouponRepository.save(coupon);
            issued.add(userId);
        }

        // 발급 완료된 사용자들을 대기열(ZSET)에서 제거
        userCouponRepository.removeCouponCandidates(couponId, issued);
    }
}
