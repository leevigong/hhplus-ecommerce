package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.UserCouponCriteria;
import kr.hhplus.be.server.application.coupon.UserCouponFacade;
import kr.hhplus.be.server.domain.userCoupon.UserCouponInfo;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import kr.hhplus.be.server.interfaces.coupon.dto.UserCouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.UserCouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerDocs {

    private final UserCouponService userCouponService;
    private final UserCouponFacade userCouponFacade;

    public CouponController(UserCouponService userCouponService,
                            UserCouponFacade userCouponFacade) {
        this.userCouponService = userCouponService;
        this.userCouponFacade = userCouponFacade;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(
            @PathVariable("userId") Long userId
    ) {
        List<UserCouponInfo> infos = userCouponService.getUserCoupons(userId);

        return ResponseEntity.ok(infos.stream()
                .map(UserCouponResponse::from)
                .toList());
    }

    @PostMapping("/issue")
    public ResponseEntity<Void> issueCoupon(
            @RequestBody UserCouponRequest request
    ) {
        UserCouponCriteria criteria = request.toCriteria();
        userCouponFacade.issue(criteria);

        return ResponseEntity.ok().build();
    }
}
