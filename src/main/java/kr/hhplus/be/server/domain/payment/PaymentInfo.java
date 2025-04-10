package kr.hhplus.be.server.domain.payment;


public record PaymentInfo(
        Long paymentId,
        Long orderId,
        long amount
) {

    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(payment.getId(), payment.getOrderId(), payment.getAmount());
    }
}
