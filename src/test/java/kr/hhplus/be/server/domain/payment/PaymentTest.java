package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    private Long orderId;

    @BeforeEach
    void setUp() {
        orderId = 1L;
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 10})
    void 결제_생성_성공(long amount) {
        Payment payment = Payment.create(orderId, amount);

        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(amount);
    }

    @ParameterizedTest
    @ValueSource(longs = {-10, -1, 0})
    void 결제_금액이_0이거나_음수이면_예외발생(long amount) {
        assertThatThrownBy(() -> Payment.create(orderId, amount))
                .hasMessage(ApiErrorCode.INVALID_PAYMENT_AMOUNT.getMessage());
    }
}
