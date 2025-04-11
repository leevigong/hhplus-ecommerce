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

    public PaymentInfo pay(PaymentCommand.Pay command) {
        Payment payment = Payment.create(command.orderId(), command.amount());

        payment = paymentRepository.save(payment);
        return PaymentInfo.from(payment);
    }
}
