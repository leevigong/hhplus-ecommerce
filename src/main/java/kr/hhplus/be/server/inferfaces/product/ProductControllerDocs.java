package kr.hhplus.be.server.inferfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.global.ErrorResponse;
import kr.hhplus.be.server.inferfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.inferfaces.product.dto.ProductSalesRankResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품", description = "상품 조회/인기상품 조회 API")
public interface ProductControllerDocs {

    @Operation(
            summary = "상품 조회",
            description = "특정 상품 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "productId": 123,
                                        "productName": "나이키 에어포스",
                                        "price": 990000,
                                        "stockQuantity": 50,
                                        "category": "SHOES"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "PRODUCT_NOT_FOUND",
                                        "message": "해당 상품을 찾을 수 없습니다."
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/api/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("productId") Long productId
    );


    @Operation(
            summary = "인기 상품 조회",
            description = "특정 기간 동안 가장 많이 판매된 인기 상품 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 상품 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductSalesRankResponse.class),
                            examples = @ExampleObject(value = """
                                    [
                                        {
                                            "productId": 101,
                                            "salesCount": 150,
                                            "totalSalesPrice": 14850000,
                                            "rankingScope": "THREE_DAYS",
                                            "rankPosition": 1
                                        },
                                        {
                                            "productId": 102,
                                            "salesCount": 120,
                                            "totalSalesPrice": 10800000,
                                            "rankingScope": "THREE_DAYS",
                                            "rankPosition": 2
                                        }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 랭킹 범위)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "INVALID_SCOPE",
                                        "message": "유효하지 않은 랭킹 범위입니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "errorCode": "INTERNAL_SERVER_ERROR",
                                        "message": "서버 내부 오류가 발생했습니다."
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/api/products/sales-rank")
    public ResponseEntity<List<ProductSalesRankResponse>> getProductSalesRank(
            @RequestParam(value = "sortBy", required = false, defaultValue = "THREE_DAYS") String sortBy
    );

}
