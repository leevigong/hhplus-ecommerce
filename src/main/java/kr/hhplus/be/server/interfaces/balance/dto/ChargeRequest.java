package kr.hhplus.be.server.interfaces.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ChargeRequest(
        @Schema(description = "충전할 금액 (100 이상 1,000,000 이하)", example = "1000")
        @Size(min = 100, max = 1000000)
        long amount
) {
}
