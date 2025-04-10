package kr.hhplus.be.server.inferfaces.product.dto;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.enums.RankingScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductSalesRankResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSalesRankV1 {
        private Long productId;
        private int totalSalesCount;
        private int totalSalesPrice;
        private RankingScope rankingScope;
        private int rankPosition;

        public static ProductSalesRankV1 from(ProductSalesRank productSalesRank) {
            return new ProductSalesRankV1(productSalesRank.getProduct().getId(),
                    productSalesRank.getTotalSalesCount(),
                    productSalesRank.getTotalSalesPrice(),
                    productSalesRank.getRankingScope(),
                    productSalesRank.getRankPosition());
        }
    }
}
