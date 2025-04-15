package kr.hhplus.be.server.inferfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.inferfaces.payment.dto.PaymentResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "결제", description = "결제 처리 API")
public interface PaymentControllerDocs {

    @Operation(
            summary = "결제 처리",
            description = """
                주문에 대한 결제를 진행합니다.
                사용자가 선택한 결제 방법을 이용하여 주문을 결제하며, 결제 성공 시 주문 상태를 PAID 상태로 변경합니다.

                **프로세스**
                1. orderId를 기반으로 주문 정보 조회
                2. 사용자의 결제 수단 및 잔액 확인
                3. 결제 진행
                4. 결제 성공 시 주문 상태 PAID 상태로 업데이트 (PENDING → PAID)
                5. 결제 실패 시 주문 상태 FAILED 상태로 변경 (PENDING → FAILED)
                """
    )
    @PostMapping("/api/payments")
    public ResponseEntity<Void> processPayment(
            @RequestBody PaymentRequest request
    );


}
