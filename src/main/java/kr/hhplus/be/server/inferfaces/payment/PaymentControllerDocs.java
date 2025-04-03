package kr.hhplus.be.server.inferfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.global.ErrorResponse;
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
                3. 결제 진행 (카드, 카카오페이 등 외부 결제 시스템 연동 가능)
                4. 결제 성공 시 주문 상태 PAID 상태로 업데이트 (PENDING → PAID)
                5. 결제 실패 시 주문 상태 FAILED 상태로 변경 (PENDING → FAILED)
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "paymentId": 3001,
                                    "orderId": 123,
                                    "userId": 1,
                                    "paymentType": "KAKAO_PAY",
                                    "amount": 49900.00,
                                    "receipt": "re123-cei123-pt",
                                    "createdAt": "2024-04-04T14:45:00",
                                    "orderStatus": "PAID"
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 오류, 유효성 검사 실패)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "INVALID_PAYMENT_REQUEST",
                                    "message": "결제 요청 데이터가 유효하지 않습니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "402", description = "잔액 부족",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "INSUFFICIENT_BALANCE",
                                    "message": "사용자의 잔액이 부족합니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "ORDER_NOT_FOUND",
                                    "message": "해당 주문을 찾을 수 없습니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "409", description = "이미 결제된 주문",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "ORDER_ALREADY_PAID",
                                    "message": "해당 주문은 이미 결제되었습니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "INTERNAL_SERVER_ERROR",
                                    "message": "서버 내부 오류가 발생했습니다."
                                }
                                """)
                    ))
    })
    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request
    );


}
