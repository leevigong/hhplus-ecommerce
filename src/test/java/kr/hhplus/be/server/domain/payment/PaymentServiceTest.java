package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Long userId;
    private Long orderId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        orderId = 1L;
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 50})
    void 결제_성공(long amount) {
        // givne
        PaymentCommand.Pay command = new PaymentCommand.Pay(userId, orderId, amount);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentInfo paymentInfo = paymentService.pay(command);

        // then
        assertThat(paymentInfo).isNotNull();
        assertThat(paymentInfo.orderId()).isEqualTo(orderId);
        assertThat(paymentInfo.amount()).isEqualTo(amount);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -50})
    void 부적절한_결제금액_예외발생(long amount) {
        // given
        PaymentCommand.Pay command = new PaymentCommand.Pay(userId, orderId, amount);

        // when & then
        assertThatThrownBy(() -> paymentService.pay(command))
                .hasMessageContaining(ApiErrorCode.INVALID_PAYMENT_AMOUNT.getMessage());
    }
}
