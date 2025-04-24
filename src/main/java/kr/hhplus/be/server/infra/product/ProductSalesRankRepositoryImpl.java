package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.ProductSalesRankRepository;
import kr.hhplus.be.server.domain.product.RankingScope;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public int saveThreeDaysProductSalesRank() {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        LocalDateTime end = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(1).atStartOfDay();
        LocalDateTime start = end.minusDays(3);

        productSalesRankJpaRepository.deleteAll();

        return productSalesRankJpaRepository.savePopularProduct(
                RankingScope.THREE_DAYS,
                start,
                end,
                5   // 상위 5개 상품만
        );
    }

    @Override
    public void deleteAll() {
        productSalesRankJpaRepository.deleteAll();
    }
}
