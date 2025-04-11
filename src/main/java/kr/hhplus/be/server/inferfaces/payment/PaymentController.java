package kr.hhplus.be.server.inferfaces.payment;

import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request
    ) {
        PaymentCommand.Pay command = new PaymentCommand.Pay(request.userId(), request.orderId(), request.amount());
        PaymentInfo info = paymentService.pay(command);

        return ResponseEntity.ok(PaymentResponse.from(info));
    }
}
