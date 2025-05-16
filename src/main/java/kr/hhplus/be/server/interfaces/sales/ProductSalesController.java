package kr.hhplus.be.server.interfaces.sales;

import kr.hhplus.be.server.domain.sales.ProductSalesCommand;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductSalesController {

    private final ProductSalesService productSalesService;

    @GetMapping("/sales/popular")
    public ResponseEntity<List<ProductSalesResponse.PopularV1>> getPopularProducts(
            @RequestParam(value = "top", defaultValue = "3") int top,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        if (endDate == null) {
            endDate = LocalDate.now();               // 오늘
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(3); // 3 일 전
        }

        List<ProductSalesInfo.Popular> info = productSalesService.getProductSales(ProductSalesCommand.Popular.of(top, startDate, endDate));

        return ResponseEntity.ok(info.stream()
                .map(ProductSalesResponse.PopularV1::from)
                .toList());
    }
}
