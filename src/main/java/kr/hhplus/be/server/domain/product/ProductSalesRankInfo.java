package kr.hhplus.be.server.domain.product;

public record ProductSalesRankInfo(
        Long productId,
        int totalSalesCount,
        long totalSalesPrice,
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
