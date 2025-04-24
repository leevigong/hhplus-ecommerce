package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.RankingScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSalesRankJpaRepository extends JpaRepository<ProductSalesRank, Long> {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);
}
