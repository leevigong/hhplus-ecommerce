package kr.hhplus.be.server.domain.sales;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSalesInfo {

    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Popular {

        private Long productId;
        private Long score;

        private Popular(Long productId, Long score) {
            this.productId = productId;
            this.score = score;
        }

        public static Popular of(Long productId, Long score) {
            return new Popular(productId, score);
        }

        public static Popular from(ProductSales productSales) {
            return new Popular(productSales.getProductId(), productSales.getCount());
        }
    }
}
