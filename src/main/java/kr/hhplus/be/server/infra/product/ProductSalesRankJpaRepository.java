package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesRank;
import kr.hhplus.be.server.domain.product.RankingScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesRankJpaRepository extends JpaRepository<ProductSalesRank, Long> {

    List<ProductSalesRank> findByRankingScope(RankingScope rankingScope);

    @Query("""
                SELECT new kr.hhplus.be.server.infra.product.ProductSalesDto(
                    oi.productId, SUM(oi.quantity), SUM(o.finalPrice))
                FROM OrderItem oi
                JOIN oi.order o
                WHERE o.createdAt >= :fromDate
                GROUP BY oi.productId
                ORDER BY COUNT(oi.id) DESC
            """)
    List<ProductSalesDto> findTopSellingProducts(@Param("fromDate") LocalDateTime fromDate);

}
