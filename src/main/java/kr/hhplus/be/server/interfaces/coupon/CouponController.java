package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.UserCouponFacade;
import kr.hhplus.be.server.application.coupon.UserCouponResult;
import kr.hhplus.be.server.interfaces.coupon.dto.UserCouponRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerDocs {

    private final UserCouponFacade userCouponFacade;

    public CouponController(UserCouponFacade userCouponFacade) {
        this.userCouponFacade = userCouponFacade;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserCouponResult.Coupons> getUserCoupons(
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(userCouponFacade.getUserCoupons(userId));
    }

    @PostMapping("/issue")
    public ResponseEntity<Void> issueCoupon(
            @RequestBody UserCouponRequest.Issue request
    ) {
        userCouponFacade.issue(request.toCriteria());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/publish")
    public ResponseEntity<Void> publishCoupon(
            @RequestBody UserCouponRequest.PublishRequest request
    ) {
        userCouponFacade.requestPublishWithRedis(request.toCriteria());

        return ResponseEntity.ok().build();
    }
}
