package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.RankingScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesRankJpaRepository extends JpaRepository<ProductSalesRank, Long> {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);

    @Modifying
    @Query(value =
            "INSERT INTO product_sales_rank " +
                    "  (product_id, total_sales_count, total_sales_price, ranking_scope, rank_position, created_at, last_modified_at) " +
                    "SELECT " +
                    "    oi.product_id, " +
                    "    SUM(oi.quantity), " +
                    "    SUM(oi.quantity * oi.price), " +
                    "    :scope, " +
                    "    ROW_NUMBER() OVER (ORDER BY SUM(oi.quantity) DESC), " +
                    "    NOW(), NOW() " +
                    "FROM order_item oi " +
                    "  JOIN `order` o ON oi.order_id = o.id " +
                    "WHERE o.order_status = 'PAID' " +
                    "  AND o.created_at >= :start " +
                    "  AND o.created_at <  :end " +
                    "GROUP BY oi.product_id " +
                    "LIMIT :limit",
            nativeQuery = true
    )
    int savePopularProduct(
            @Param("scope") RankingScope scope,        // 랭킹 기간 (예: THREE_DAYS)
            @Param("start") LocalDateTime start,        // 집계 시작 시각 (inclusive)
            @Param("end") LocalDateTime end,          // 집계 종료 시각 (exclusive)
            @Param("limit") int topN          // 상위 N개 상품만 저장
    );
}
