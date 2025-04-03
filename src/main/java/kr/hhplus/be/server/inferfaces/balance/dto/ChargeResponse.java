package kr.hhplus.be.server.inferfaces.balance.dto;

import java.math.BigDecimal;

public record ChargeResponse(
        BigDecimal amount
) {
}
