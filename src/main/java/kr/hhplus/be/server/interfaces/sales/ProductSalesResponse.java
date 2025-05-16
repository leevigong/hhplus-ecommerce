package kr.hhplus.be.server.interfaces.sales;

import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProductSalesResponse {

    @Getter
    @NoArgsConstructor
    public static class PopularV1 {

        private Long productId;
        private Long salesCount;

        private PopularV1(Long productId, Long salesCount) {
            this.productId = productId;
            this.salesCount = salesCount;
        }

        public static PopularV1 from(ProductSalesInfo.Popular info) {
            return new PopularV1(info.getProductId(), info.getScore());
        }
    }
}
