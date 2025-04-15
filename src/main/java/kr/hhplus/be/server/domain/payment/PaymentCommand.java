package kr.hhplus.be.server.domain.payment;

public record PaymentCommand(
        Long userId,
        Long orderId,
        long amount
) {
    public static PaymentCommand of(Long userId, Long orderId, long amount) {
        return new PaymentCommand(userId, orderId, amount);
    }
}
