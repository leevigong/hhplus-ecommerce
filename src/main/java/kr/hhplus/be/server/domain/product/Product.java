package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.enums.Category;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static Product create(String name, int price, int stockQuantity, Category category) {
        validateName(name);
        validatePrice(price);

        return Product.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .category(category)
                .build();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
    }

    public static void validatePrice(int price) {
        if (price <= 0) {
            throw new ApiException(ApiErrorCode.INVALID_PRODUCT_PRICE);
        }
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new ApiException(ApiErrorCode.INVALID_STOCK_INCREASE_AMOUNT);
        }
        this.stockQuantity += quantity;
    }

    public void subStock(int quantity) {
        validateStockQuantity(quantity);

        this.stockQuantity -= quantity;
    }

    public void validateStockQuantity(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new ApiException(ApiErrorCode.INSUFFICIENT_STOCK);
        }
    }

}
