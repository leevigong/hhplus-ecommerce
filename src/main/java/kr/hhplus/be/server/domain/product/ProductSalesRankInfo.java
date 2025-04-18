package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.RankingScope;

public record ProductSalesRankInfo(
        Long productId,
        int totalSalesCount,
        int totalSalesPrice,
        RankingScope rankingScope,
        int rankPosition
) {
    public static ProductSalesRankInfo from(ProductSalesRank productSalesRank) {
        return new ProductSalesRankInfo(
                productSalesRank.getProduct().getId(),
                productSalesRank.getTotalSalesCount(),
                productSalesRank.getTotalSalesPrice(),
                productSalesRank.getRankingScope(),
                productSalesRank.getRankPosition()
        );
    }
}
