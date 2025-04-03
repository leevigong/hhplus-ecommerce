package kr.hhplus.be.server.inferfaces.balance.dto;

import kr.hhplus.be.server.domain.balance.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserBalanceHistoryResponse(
        Long userId,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal before_balance,
        BigDecimal after_balance,
        LocalDateTime createdAt
) {
}

