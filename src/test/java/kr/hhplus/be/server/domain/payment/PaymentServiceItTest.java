package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PaymentServiceItTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 결제_성공() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        long payAmount = 100L;
        PaymentCommand command = new PaymentCommand(userId, orderId, payAmount);

        // when
        PaymentInfo paymentInfo = paymentService.create(command);

        // then
        Payment payment = paymentRepository.getById(paymentInfo.paymentId());
        assertThat(payment).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(payAmount);
    }

}
