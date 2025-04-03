package kr.hhplus.be.server.inferfaces.product.dto;

import kr.hhplus.be.server.domain.product.Category;

public record ProductResponse(
        Long productId,
        String productName,
        int price,
        int stockQuantity,
        Category category
) {
}
