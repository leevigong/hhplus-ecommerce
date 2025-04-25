package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infra.product.ProductSalesDto;

import java.util.List;

public interface ProductSalesRankRepository {

    ProductSalesRank save(ProductSalesRank productSalesRank);

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);

    List<ProductSalesDto> saveThreeDaysProductSalesRank();

    void deleteAll();
}
