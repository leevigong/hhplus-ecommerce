package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.Category;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Sample Product")
                .price(100)
                .stockQuantity(10)
                .category(Category.TOP)
                .build();
    }

    @Test
    void 상품의_재고가_충분할_때_재고_감소_성공() {
        product.subStock(5);

        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void 상품의_재고보다_많은_수량_감소_시도시_예외_발생() {
        assertThatThrownBy(() -> product.subStock(15))
                .isInstanceOf(ApiException.class)
                .hasMessage(ApiErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    void 재고에_양의_정수를_추가하면_재고가_정상적으로_증가() {
        product.addStock(5);

        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 재고에_음수_또는_0을_추가하려고_하면_예외_발생(int value) {
        assertThatThrownBy(() -> product.addStock(value))
                .isInstanceOf(ApiException.class)
                .hasMessage(ApiErrorCode.INVALID_STOCK_INCREASE_AMOUNT.getMessage());
    }

    @Test
    void 상품_가격이_0보다_크면_검증을_성공() {
        assertThatCode(() -> product.validatePrice())
                .doesNotThrowAnyException();
    }

    @Test
    void 상품_가격이_0이하면_예외가_발생() {
        product = Product.builder()
                .id(2L)
                .name("Invalid Price Product")
                .price(0)
                .stockQuantity(10)
                .category(Category.TOP)
                .build();

        assertThatThrownBy(() -> product.validatePrice())
                .isInstanceOf(ApiException.class)
                .hasMessage(ApiErrorCode.INVALID_PRODUCT_PRICE.getMessage());
    }
}
