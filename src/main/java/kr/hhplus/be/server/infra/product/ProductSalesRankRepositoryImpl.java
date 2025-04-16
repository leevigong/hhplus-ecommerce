package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.ProductSalesRankRepository;
import kr.hhplus.be.server.domain.product.enums.RankingScope;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductSalesRankRepositoryImpl implements ProductSalesRankRepository {


    @Override
    public List<ProductSalesRank> findByRankingScope(RankingScope rankingScope) {
        return List.of();
    }
}
