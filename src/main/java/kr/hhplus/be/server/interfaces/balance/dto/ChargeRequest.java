package kr.hhplus.be.server.interfaces.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ChargeRequest(
        @Schema(description = "충전할 금액 (100 이상 1,000,000 이하)", example = "1000")
        @Min(100)              // 최소 100원
        @Max(1_000_000)        // 최대 1,000,000원
        long amount
) {
}
