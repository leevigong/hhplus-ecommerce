package kr.hhplus.be.server.inferfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderCreateRequest(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "주문 항목 목록", example = """
                [
                    {
                        "productId": 1,
                        "quantity": 2
                    }
                ]
                """)
        List<OrderProductRequest> orderProducts,

        @Schema(description = "사용자 쿠폰 ID", example = "1")
        Long userCouponId
) {
        public record OrderProductRequest(
                Long productId,
                int quantity
        ) {
        }
}
