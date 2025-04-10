package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.Category;

public record ProductInfo(
        Long productId,
        String productName,
        int price,
        int stockQuantity,
        Category category
) {
    public static ProductInfo from(Product product) {
        return new ProductInfo(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory()
        );
    }
}
