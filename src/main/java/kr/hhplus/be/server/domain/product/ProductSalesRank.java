package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.enums.RankingScope;
import kr.hhplus.be.server.global.entity.BaseEntity;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_sales_rank")
public class ProductSalesRank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int totalSalesCount; // 기간내 총 판매 수량

    private long totalSalesPrice; // 기간내 총 판매 금액

    @Enumerated(EnumType.STRING)
    private RankingScope rankingScope; // 랭킹 기간(범위)

    private int rankPosition; // 랭킹 순위

    public static ProductSalesRank create(Product product, int totalSalesCount, int totalSalesPrice, RankingScope rankingScope, int rankPosition) {
        return ProductSalesRank.builder()
                .product(product)
                .totalSalesCount(totalSalesCount)
                .totalSalesPrice(totalSalesPrice)
                .rankingScope(rankingScope)
                .rankPosition(rankPosition)
                .build();
    }
}
