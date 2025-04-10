package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.RankingScope;

import java.util.List;

public interface ProductSalesRankRepository {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);
}
