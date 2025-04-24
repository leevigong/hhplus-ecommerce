package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface ProductSalesRankRepository {

    ProductSalesRank save(ProductSalesRank productSalesRank);

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);

    int saveThreeDaysProductSalesRank();

    void deleteAll();
}
