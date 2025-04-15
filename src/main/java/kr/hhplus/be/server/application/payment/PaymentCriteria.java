package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.payment.PaymentCommand;

public record PaymentCriteria(
        Long userId,
        Long orderId,
        long finalPrice
) {

    public PaymentCommand toPaymentCommand() {
        return PaymentCommand.of(
                userId(),
                orderId(),
                finalPrice()
        );
    }
}
