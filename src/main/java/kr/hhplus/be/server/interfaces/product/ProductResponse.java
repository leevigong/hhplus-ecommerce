package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.Category;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductSalesRankInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductResponse {

    @Getter
    @NoArgsConstructor
    public static class ProductV1 {

        private Long productId;
        private String productName;
        private int price;
        private int stockQuantity;
        private Category category;

        private ProductV1(Long productId, String productName, int price, int stockQuantity, Category category) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.category = category;
        }

        public static ProductV1 from(ProductInfo productInfo) {
            return new ProductV1(
                    productInfo.productId(),
                    productInfo.productName(),
                    productInfo.price(),
                    productInfo.stockQuantity(),
                    productInfo.category()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PopularV1 {

        private Long productId;
        private int totalSalesCount;
        private long totalSalesPrice;
        private String rankingScope;
        private int rankPosition;

        private PopularV1(Long productId,
                          int totalSalesCount,
                          long totalSalesPrice,
                          String rankingScope,
                          int rankPosition) {
            this.productId = productId;
            this.totalSalesCount = totalSalesCount;
            this.totalSalesPrice = totalSalesPrice;
            this.rankingScope = rankingScope;
            this.rankPosition = rankPosition;
        }

        public static PopularV1 from(ProductSalesRankInfo info) {
            return new PopularV1(
                    info.productId(),
                    info.totalSalesCount(),
                    info.totalSalesPrice(),
                    info.rankingScope().name(),
                    info.rankPosition()
            );
        }
    }
}
