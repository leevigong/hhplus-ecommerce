package kr.hhplus.be.server.inferfaces.payment.dto;

import kr.hhplus.be.server.domain.payment.PaymentInfo;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        long amount
) {

    public static PaymentResponse from(PaymentInfo paymentInfo) {
        return new PaymentResponse(
                paymentInfo.paymentId(),
                paymentInfo.orderId(),
                paymentInfo.amount()
        );
    }
}
