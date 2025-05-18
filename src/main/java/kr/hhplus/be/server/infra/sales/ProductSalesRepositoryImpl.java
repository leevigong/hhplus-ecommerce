package kr.hhplus.be.server.infra.sales;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.sales.ProductSales;
import kr.hhplus.be.server.domain.sales.ProductSalesCommand;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.domain.sales.ProductSalesRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ProductSalesRepositoryImpl implements ProductSalesRepository {

    private final ProductSalesJpaRepository productSalesJpaRepository;
    private final ProductSalesRedisCache productSalesRedisCache;

    public ProductSalesRepositoryImpl(ProductSalesJpaRepository productSalesJpaRepository,
                                      ProductSalesRedisCache productSalesRedisCache) {
        this.productSalesJpaRepository = productSalesJpaRepository;
        this.productSalesRedisCache = productSalesRedisCache;
    }

    @Override
    public ProductSales save(ProductSales productSales) {
        return productSalesJpaRepository.save(productSales);
    }

    @Override
    public List<ProductSales> findPopulars(ProductSalesCommand.Popular command) {
        return productSalesJpaRepository.findRankBetweenDates(command.getStartDate(), command.getEndDate());
    }

    @Override
    public void saveAll(List<ProductSales> productSales) {
        productSalesJpaRepository.saveAll(productSales);
    }

    @Override
    public void add(List<OrderItem> items) {
        productSalesRedisCache.add(items);
    }

    @Override
    public List<ProductSalesInfo.Popular> getTopSalesRange(LocalDate startDate, LocalDate endDate, int top) {
        return productSalesRedisCache.getTopSalesRange(startDate, endDate, top);
    }

    @Override
    public List<ProductSalesInfo.Popular> getDailySales(LocalDate date) {
        return productSalesRedisCache.getAllSales(date);
    }
}
