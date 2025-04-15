package kr.hhplus.be.server.inferfaces.payment;

import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentFacade paymentFacade;

    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }


    @PostMapping
    public ResponseEntity<Void> processPayment(
            @RequestBody PaymentRequest request
    ) {
        PaymentCriteria criteria = request.toCriteria();
        paymentFacade.pay(criteria);
        
        return ResponseEntity.ok().build();
    }
}
