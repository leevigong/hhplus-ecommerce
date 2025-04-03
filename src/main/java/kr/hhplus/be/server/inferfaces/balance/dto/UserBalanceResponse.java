package kr.hhplus.be.server.inferfaces.balance.dto;

import java.math.BigDecimal;

public record UserBalanceResponse(
        Long userId,
        String userName,
        BigDecimal balance
) {
}

