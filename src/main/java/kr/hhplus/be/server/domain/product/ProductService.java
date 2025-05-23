package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.support.cache.CacheNames;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PRODUCT, key = "#productId")
    public ProductInfo getProductById(Long productId) {
        Product product = productRepository.getById(productId);

        return ProductInfo.from(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.POPULAR_PRODUCTS, key = "#rankingScope")
    public List<ProductSalesRankInfo> getProductSalesRank(String rankingScope) {
        List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

        return productSalesRanks.stream()
                .map(rank -> ProductSalesRankInfo.from(rank))
                .collect(Collectors.toList());
    }

    @Transactional
    public void validateAndSubStockProducts(List<OrderCommand.CreateOrderItem> createOrderItems) {
        for (OrderCommand.CreateOrderItem orderItem : createOrderItems) {
            // 상품을 조회
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_PRODUCT));

            // 재고 차감
            product.subStock(orderItem.getQuantity());
        }
    }
}
