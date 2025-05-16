package kr.hhplus.be.server.domain.sales;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private long count;          // 판매 수량

    private LocalDate salesDate; // 판매 일자

    @Builder
    private ProductSales(Long productId, long count, LocalDate salesDate) {
        this.productId = productId;
        this.count = count;
        this.salesDate = salesDate;
    }

    public static ProductSales create(Long productId, long count) {
        return ProductSales.builder()
                .productId(productId)
                .count(count)
                .build();
    }

    public static ProductSales createWithDate(Long productId, long count, LocalDate salesDate) {
        return ProductSales.builder()
                .productId(productId)
                .count(count)
                .salesDate(salesDate)
                .build();
    }

}
