package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface ProductSalesRankRepository {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);
}
