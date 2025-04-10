package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.RankingScope;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSalesRankRepository {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);
}
