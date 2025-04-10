package kr.hhplus.be.server.inferfaces.balance;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import kr.hhplus.be.server.inferfaces.balance.dto.ChargeRequest;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceHistoryResponse;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "잔액", description = "잔액 조회/충전/내역조회 API")
@RequestMapping("/api/v1/balance")
public interface UserBalanceControllerDocs {

    @Operation(summary = "사용자 잔액 조회",
            description = "사용자 현재 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserBalanceResponse.UserBalanceV1.class),
                            examples = @ExampleObject(value = """
                                {
                                    "userId": 1,
                                    "userName": "이다은",
                                    "amount": 10000
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
    ResponseEntity<UserBalanceResponse.UserBalanceV1> getBalance(
            @PathVariable("userId") Long userId
    );

    @Operation(summary = "사용자 잔액 충전",
            description = "사용자 잔액을 충전합니다. 충전 성공 시 갱신된 포인트 정보가 반환됩니다. <br> " +
                    "단, 충전 금액은 1 이상이어야 하며, 최대 한도(100,000)를 초과할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 충전 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserBalanceResponse.UserBalanceV1.class),
                            examples = @ExampleObject(value = """
                                {
                                    "userId": 1,
                                    "userName": "이다은",
                                    "balance": 6000.00
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
            @ApiResponse(responseCode = "400", description = "잘못된 충전 요청 금액 (1보다 작거나 최대 한도 초과)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "errorCode": "INVALID_AMOUNT",
                                    "message": "충전 금액은 1 이상 100,000 이하만 가능합니다."
                                }
                                """)
                    ))
    })
    ResponseEntity<UserBalanceResponse.UserBalanceV1> chargeBalance(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ChargeRequest request
    );

    @Operation(summary = "사용자 잔액 내역 조회",
            description = "특정 사용자의 잔액 변동 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 잔액 내역 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserBalanceHistoryResponse.class)),
                            examples = @ExampleObject(value = """
                                [
                                    {
                                        "userId": 1,
                                        "transactionType": "CHARGE",
                                        "amount": 5000.00,
                                        "before_balance": 10000.00,
                                        "after_balance": 15000.00,
                                        "createdAt": "2024-04-03T12:34:56"
                                    },
                                    {
                                        "userId": 1,
                                        "transactionType": "PAYMENT",
                                        "amount": 2000.00,
                                        "before_balance": 15000.00,
                                        "after_balance": 13000.00,
                                        "createdAt": "2024-04-03T12:40:00"
                                    }
                                ]
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
    ResponseEntity<List<UserBalanceHistoryResponse.UserBalanceHistoryV1>> getUserBalanceHistory(
            @PathVariable("userId") Long userId
    );
}
