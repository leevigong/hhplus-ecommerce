package kr.hhplus.be.server.infra.sales;

import kr.hhplus.be.server.domain.sales.ProductSales;
import kr.hhplus.be.server.domain.sales.ProductSalesCommand;
import kr.hhplus.be.server.domain.sales.ProductSalesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductSalesRepositoryImpl implements ProductSalesRepository {

    private final ProductSalesJpaRepository productSalesJpaRepository;

    public ProductSalesRepositoryImpl(ProductSalesJpaRepository productSalesJpaRepository) {
        this.productSalesJpaRepository = productSalesJpaRepository;
    }

    @Override
    public ProductSales save(ProductSales productSales) {
        return productSalesJpaRepository.save(productSales);
    }

    @Override
    public List<ProductSales> findPopulars(ProductSalesCommand.Popular command) {
        return productSalesJpaRepository.findRankBetweenDates(command.getStartDate(), command.getEndDate());
    }
}
