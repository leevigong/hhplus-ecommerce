package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.enums.RankingScope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSalesRankRepository productSalesRankRepository;

    public ProductService(ProductRepository productRepository,
                          ProductSalesRankRepository productSalesRankRepository) {
        this.productRepository = productRepository;
        this.productSalesRankRepository = productSalesRankRepository;
    }

    public ProductInfo getProductById(Long productId) {
        Product product = productRepository.findById(productId);

        return ProductInfo.from(product);
    }

    public List<ProductSalesRankInfo> getProductSalesRank(String rankingScope) {
        List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

        return productSalesRanks.stream()
                .map(rank -> ProductSalesRankInfo.from(rank))
                .collect(Collectors.toList());
    }

    public void validateAndSubStockProducts(List<OrderCommand.CreateOrderItem> createOrderItems) {
        for (OrderCommand.CreateOrderItem orderItem : createOrderItems) {
            // 상품을 조회
            Product product = productRepository.findById(orderItem.getProductId());

            // 재고 및 상품 상태 확인
            product.validateStockQuantity(orderItem.getQuantity());

            // 재고 차감
            product.subStock(orderItem.getQuantity());
        }
    }

}

