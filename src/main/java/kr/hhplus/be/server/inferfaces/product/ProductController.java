package kr.hhplus.be.server.inferfaces.product;

import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductSalesRankInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductInfo> getProduct(
            @PathVariable("productId") Long productId
    ) {
        ProductInfo response = productService.getProductById(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductSalesRankInfo>> getProductSalesRank(
            @RequestParam(value = "rankingScope", required = false, defaultValue = "THREE_DAYS") String rankingScope
    ) {
        List<ProductSalesRankInfo> responses = productService.getProductSalesRank(rankingScope);
        return ResponseEntity.ok(responses);
    }
}
