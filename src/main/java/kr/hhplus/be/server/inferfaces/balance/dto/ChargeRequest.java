package kr.hhplus.be.server.inferfaces.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ChargeRequest(
        @Schema(description = "충전할 금액 (1 이상 100,000 이하)", example = "1000")
        BigDecimal amount
) {
}
