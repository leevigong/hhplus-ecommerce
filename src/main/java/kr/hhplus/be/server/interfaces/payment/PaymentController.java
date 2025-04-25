package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentRequest;
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

    // 주문/결제 에러로 결제만 따로 해야하는 경우를 위한 API
    @PostMapping
    public ResponseEntity<Void> processPayment(
            @RequestBody PaymentRequest request
    ) {
        PaymentCriteria criteria = request.toCriteria();
        paymentFacade.pay(criteria);
        
        return ResponseEntity.ok().build();
    }
}
