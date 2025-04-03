package kr.hhplus.be.server.inferfaces.payment;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentControllerDocs {

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = new PaymentResponse(3001L, 123L, 1L, PaymentType.KAKAO_PAY, BigDecimal.valueOf(49900), "re123-cei123-pt", LocalDateTime.now().withNano(0), OrderStatus.PAID);

        return ResponseEntity.ok(response);
    }
}
