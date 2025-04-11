package kr.hhplus.be.server.inferfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import kr.hhplus.be.server.inferfaces.coupon.dto.CouponIssueResponse;
import kr.hhplus.be.server.inferfaces.coupon.dto.UserCouponRequest;
import kr.hhplus.be.server.inferfaces.coupon.dto.UserCouponResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "쿠폰", description = "쿠폰 조회/발급 API")
public interface CouponControllerDocs {

    @Operation(summary = "사용자 쿠폰 리스트 조회",
            description = "사용자의 쿠폰 목록을 조회합니다. \n\n\n" +
                    "discountType가 FIXED인 경우 usedAt는 null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserCouponResponse.class)),
                            examples = @ExampleObject(value = """
                                [
                                    {
                                        "couponId": 1,
                                        "couponCode": "HANGHAE8888",
                                        "couponStatus": "AVAILABLE",
                                        "discountType": "FIXED",
                                        "discountAmount": 1000.00,
                                        "createdAt": "2024-04-01T10:00:00",
                                        "usedAt": null,
                                        "expiredAt": "2024-06-30T23:59:59"
                                    },
                                    {
                                        "couponId": 2,
                                        "couponCode": "SPARTA1000",
                                        "couponStatus": "USED",
                                        "discountType": "PERCENTAGE",
                                        "discountAmount": 20.00,
                                        "createdAt": "2024-03-20T12:00:00",
                                        "usedAt": "2024-03-25T14:45:00",
                                        "expiredAt": "2024-04-30T23:59:59"
                                    }
                                ]
                                """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "USER_NOT_FOUND",
                                    "message": "해당 사용자를 찾을 수 없습니다."
                                }
                                """)
                    ))
    })
    ResponseEntity<List<UserCouponResponse>> getUserCoupons(
            @PathVariable("userId") Long userId
    );

    @Operation(summary = "(선착순) 쿠폰 발급",
            description = "(선착순) 쿠폰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CouponIssueResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "couponId": 1,
                                    "couponCode": "HANGHAE8888",
                                    "couponStatus": "AVAILABLE",
                                    "discountType": "FIXED",
                                    "discountAmount": 1000.00,
                                    "expiredAt": "2024-06-30T23:59:59",
                                    "createdAt": "2024-04-01T10:00:00"
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "USER_NOT_FOUND",
                                    "message": "해당 사용자를 찾을 수 없습니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 쿠폰",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "INVALID_COUPON",
                                    "message": "해당 쿠폰을 발급할 수 없습니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "409", description = "발급 가능한 쿠폰 수량 부족",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "COUPON_OUT_OF_STOCK",
                                    "message": "모든 쿠폰이 발급 완료되었습니다."
                                }
                                """)
                    ))
    })
    ResponseEntity<CouponIssueResponse> issueCoupon(
            @RequestBody UserCouponRequest request
    );

}
