package kr.hhplus.be.server.infra.sales;

import kr.hhplus.be.server.domain.sales.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProductSalesJpaRepository extends JpaRepository<ProductSales, Long> {

    /**
     * 기간(startDate ~ endDate) 내의 상품 판매 랭킹을 조회.
     */
    @Query("SELECT r FROM ProductSales r WHERE r.salesDate BETWEEN :startDate AND :endDate ORDER BY r.count DESC")
    List<ProductSales> findRankBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
