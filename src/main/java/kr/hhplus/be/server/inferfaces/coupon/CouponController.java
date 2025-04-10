package kr.hhplus.be.server.inferfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCouponCommand;
import kr.hhplus.be.server.domain.coupon.UserCouponInfo;
import kr.hhplus.be.server.inferfaces.coupon.dto.CouponIssueResponse;
import kr.hhplus.be.server.inferfaces.coupon.dto.UserCouponRequest;
import kr.hhplus.be.server.inferfaces.coupon.dto.UserCouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerDocs {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(
            @PathVariable("userId") Long userId
    ) {
        List<UserCouponInfo> infos = couponService.getUserCoupons(userId);

        return ResponseEntity.ok(infos.stream()
                .map(UserCouponResponse::from)
                .toList());
    }

    @PostMapping("/issue")
    public ResponseEntity<CouponIssueResponse> issueCoupon(
            @RequestBody UserCouponRequest request
    ) {
        UserCouponCommand command = UserCouponCommand.of(request.couponId(), request.userId());
        UserCouponInfo info = couponService.issueCoupon(command);

        return ResponseEntity.ok(CouponIssueResponse.from(info));
    }

}
