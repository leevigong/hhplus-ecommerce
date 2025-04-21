package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.enums.Category;
import kr.hhplus.be.server.support.entity.BaseEntity;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    private Product(String name, int price, int stockQuantity, Category category) {
        validateName(name);
        validatePrice(price);

        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }


    public static Product create(String name, int price, int stockQuantity, Category category) {
        return Product.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .category(category)
                .build();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ApiException(ApiErrorCode.INVALID_PRODUCT_NAME);
        }
    }

    public void validatePrice(int price) {
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
