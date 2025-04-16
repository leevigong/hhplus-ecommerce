package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductSalesRankInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.interfaces.product.dto.ProductSalesRankResponse;
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
    public ResponseEntity<List<ProductSalesRankResponse.ProductSalesRankV1>> getProductSalesRank(
            @RequestParam(value = "rankingScope", required = false, defaultValue = "THREE_DAYS") String rankingScope
    ) {
        List<ProductSalesRankInfo> infos = productService.getProductSalesRank(rankingScope);

        return ResponseEntity.ok(infos.stream()
                .map(ProductSalesRankResponse.ProductSalesRankV1::from)
                .toList());
    }
}
