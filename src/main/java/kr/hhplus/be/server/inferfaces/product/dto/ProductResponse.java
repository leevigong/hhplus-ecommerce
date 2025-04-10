package kr.hhplus.be.server.inferfaces.product.dto;

import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductV1 {
        private Long productId;
        private String productName;
        private int price;
        private int stockQuantity;
        private Category category;

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
}
