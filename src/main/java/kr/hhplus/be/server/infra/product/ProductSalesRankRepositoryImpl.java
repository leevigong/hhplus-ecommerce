package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.ProductSalesRankRepository;
import kr.hhplus.be.server.domain.product.RankingScope;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductSalesRankRepositoryImpl implements ProductSalesRankRepository {

    private final ProductSalesRankJpaRepository productSalesRankJpaRepository;

    public ProductSalesRankRepositoryImpl(ProductSalesRankJpaRepository productSalesRankJpaRepository) {
        this.productSalesRankJpaRepository = productSalesRankJpaRepository;
    }

    @Override
    public ProductSalesRank save(ProductSalesRank productSalesRank) {
        return productSalesRankJpaRepository.save(productSalesRank);
    }

    @Override
    public List<ProductSalesRank> findByRankingScope(RankingScope rankingScope) {
        return productSalesRankJpaRepository.findByRankingScope(rankingScope);
    }

    @Override
    public List<ProductSalesDto> saveThreeDaysProductSalesRank() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        return productSalesRankJpaRepository.findTopSellingProducts(threeDaysAgo);
    }

    @Override
    public void deleteAll() {
        productSalesRankJpaRepository.deleteAll();
    }
}
