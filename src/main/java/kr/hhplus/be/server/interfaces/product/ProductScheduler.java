package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProductScheduler {

    private final ProductService productService;

    public ProductScheduler(ProductService productService) {
        this.productService = productService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public int saveThreeDaysProductSalesRank() {
        return productService.saveThreeDaysProductSalesRank();
    }
}
