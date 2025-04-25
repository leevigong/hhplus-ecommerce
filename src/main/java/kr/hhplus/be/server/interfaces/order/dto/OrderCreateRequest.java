package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.order.OrderCriteria;

import java.util.List;
import java.util.stream.Collectors;

public record OrderCreateRequest(
        @Schema(description = "유저ID", example = "1")
        @NotNull
        Long userId,

        @Schema(description = "주문 상품 목록")
        @NotEmpty
        List<OrderItemRequest> orderItems,

        @Schema(
                description = "쿠폰ID (선택, 없으면 null)",
                example = "1",
                nullable = true
        )
        Long userCouponId
) {

    public OrderCriteria.Create toCriteria() {
        List<OrderCriteria.OrderItem> items = this.orderItems.stream()
                .map(item -> new OrderCriteria.OrderItem(item.productId(), item.quantity(), item.price()))
                .collect(Collectors.toList());

        return new OrderCriteria.Create(this.userId, items, this.userCouponId);
    }

    public record OrderItemRequest(
            @Schema(description = "상품ID", example = "1")
            @NotNull
            Long productId,

            @Schema(description = "구매수량", example = "2")
            @NotNull
            Integer quantity,

            @Schema(description = "가격", example = "1000")
            @NotNull
            Long price
    ) {
    }
}
