package kr.hhplus.be.server.interfaces.product;

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
    public ResponseEntity<ProductResponse.ProductV1> getProduct(
            @PathVariable("productId") Long productId
    ) {
        ProductInfo info = productService.getProductById(productId);

        return ResponseEntity.ok(ProductResponse.ProductV1.from(info));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse.PopularV1>> getProductSalesRank(
            @RequestParam(value = "rankingScope", required = false, defaultValue = "THREE_DAYS") String rankingScope
    ) {
        List<ProductSalesRankInfo> infos = productService.getProductSalesRank(rankingScope);

        List<ProductResponse.PopularV1> response = infos.stream()
                .map(ProductResponse.PopularV1::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
