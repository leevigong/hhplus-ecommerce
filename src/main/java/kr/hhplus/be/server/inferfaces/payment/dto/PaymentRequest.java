package kr.hhplus.be.server.inferfaces.payment.dto;

import kr.hhplus.be.server.domain.payment.PaymentCommand;

public record PaymentRequest(
        Long orderId,
        Long userId,
        long amount
) {

    public PaymentCommand.Pay toCommand() {
        return new PaymentCommand.Pay(userId, orderId, amount);
    }
}

