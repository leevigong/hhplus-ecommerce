package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.support.exception.ErrorResponse;
import kr.hhplus.be.server.interfaces.order.dto.OrderCreateRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주문", description = "주문 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "주문 생성 및 결제 요청",
            description = "상품 주문과 결제를 처리합니다. 주문 생성 후 결제를 시도하며, 결제 시도 시 주문은 PENDING 상태로 생성됩니다.\n\n" +
                    "**프로세스**\n" +
                    "1. 상품 재고 확인\n" +
                    "2. 쿠폰 적용 및 할인 계산\n" +
                    "3. 주문 정보 저장 (주문 PENDING 상태)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "orderId": 12345,
                                        "finalPrice": 49900.00,
                                        "orderedAt": "2024-04-04T14:30:00"
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 주문 요청 (입력값 오류, 유효성 검사 실패)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "INVALID_ORDER",
                                        "message": "주문 요청 데이터가 유효하지 않습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "PRODUCT_NOT_FOUND",
                                        "message": "주문하려는 상품을 찾을 수 없습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "USER_NOT_FOUND",
                                        "message": "해당 사용자를 찾을 수 없습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "409", description = "재고 부족",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "SOLD_OUT", value = """
                                            {
                                                "errorCode": "SOLD_OUT",
                                                "message": "해당 상품이 품절되었습니다."
                                            }
                                            """),
                                    @ExampleObject(name = "INSUFFICIENT_STOCK", value = """
                                            {
                                                "errorCode": "INSUFFICIENT_STOCK",
                                                "message": "선택한 상품의 재고가 부족합니다."
                                            }
                                            """)
                            }
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
    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request
    );


}
