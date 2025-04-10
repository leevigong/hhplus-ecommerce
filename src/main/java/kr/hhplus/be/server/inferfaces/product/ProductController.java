package kr.hhplus.be.server.inferfaces.product;

import kr.hhplus.be.server.domain.product.Category;
import kr.hhplus.be.server.domain.product.RankingScope;
import kr.hhplus.be.server.inferfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.inferfaces.product.dto.ProductSalesRankResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements ProductControllerDocs {

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("productId") Long productId
    ) {
        ProductResponse response = new ProductResponse(1L, "스투시 후드티", 100000, 10, Category.TOP);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductSalesRankResponse>> getProductSalesRank(
            @RequestParam(value = "sortBy", required = false, defaultValue = "THREE_DAYS") String sortBy
    ) {

        ProductSalesRankResponse top1 = new ProductSalesRankResponse(1L, 40, 99999999, RankingScope.THREE_DAYS, 1);
        ProductSalesRankResponse top2 = new ProductSalesRankResponse(2L, 20, 55555555, RankingScope.THREE_DAYS, 2);
        ProductSalesRankResponse top3 = new ProductSalesRankResponse(3L, 10, 10000000, RankingScope.THREE_DAYS, 3);
        List<ProductSalesRankResponse> responses = List.of(top1, top2, top3);

        return ResponseEntity.ok(responses);
    }
}
