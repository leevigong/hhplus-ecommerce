package kr.hhplus.be.server.domain.sales;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductSalesService {

    private final ProductSalesRepository productSalesRepository;

    public ProductSalesService(ProductSalesRepository productSalesRepository) {
        this.productSalesRepository = productSalesRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.POPULAR_PRODUCT_SALES, keyGenerator = "popularProductsKeyGenerator")
    public List<ProductSalesInfo.Popular> getProductSales(ProductSalesCommand.Popular command) {
        return productSalesRepository.getTopSalesRange(command.getStartDate(), command.getEndDate(), command.getTop());
    }

    public void add(List<OrderItem> items) {
        productSalesRepository.add(items);
    }

    public void saveYesterdaySales() {
        LocalDate date = LocalDate.now().minusDays(1);
        List<ProductSalesInfo.Popular> dailySales = productSalesRepository.getDailySales(date);
        if (dailySales.isEmpty()) {
            return;
        }

        List<ProductSales> entities = dailySales.stream()
                .map(sale -> ProductSales.create(sale.getProductId(), sale.getScore()))
                .toList();
        productSalesRepository.saveAll(entities);
    }
}
