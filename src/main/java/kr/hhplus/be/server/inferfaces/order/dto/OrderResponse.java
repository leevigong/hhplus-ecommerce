package kr.hhplus.be.server.inferfaces.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        long orderId,
        BigDecimal finalPrice,
        LocalDateTime orderedAt
) {
}
