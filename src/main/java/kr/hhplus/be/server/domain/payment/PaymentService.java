package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentInfo create(PaymentCommand command) {
        Payment payment = paymentRepository.save(Payment.create(command.orderId(), command.amount()));

        return PaymentInfo.from(payment);
    }
}
