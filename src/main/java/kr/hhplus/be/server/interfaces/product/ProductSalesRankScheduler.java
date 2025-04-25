package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.infra.product.ProductSalesDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductSalesRankScheduler {

    private final ProductRepository productRepository;
    private final ProductSalesRankRepository productSalesRankRepository;

    public ProductSalesRankScheduler(OrderItemRepository orderItemRepository,
                                     ProductRepository productRepository,
                                     ProductSalesRankRepository productSalesRankRepository) {
        this.productRepository = productRepository;
        this.productSalesRankRepository = productSalesRankRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void generate3DaySalesRank() {
        productSalesRankRepository.deleteAll();

        List<ProductSalesDto> topProducts =
                productSalesRankRepository.saveThreeDaysProductSalesRank()
                        .stream()
                        .sorted(Comparator.comparing(ProductSalesDto::salesCount).reversed())
                        .toList();

        int rank = 1;
        for (ProductSalesDto agg : topProducts) {
            Product product = productRepository.getById(agg.productId());

            ProductSalesRank rankEntry = ProductSalesRank.builder()
                    .product(product)
                    .totalSalesCount((int) agg.salesCount())
                    .totalSalesPrice(agg.salesPrice())
                    .rankingScope(RankingScope.THREE_DAYS)
                    .rankPosition(rank++)
                    .build();

            productSalesRankRepository.save(rankEntry);
        }
    }
}
