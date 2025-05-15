package kr.hhplus.be.server.domain.sales;

import kr.hhplus.be.server.support.cache.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<ProductSales> populars = productSalesRepository.findPopulars(command);

        return populars.stream()
                .map(ProductSalesInfo.Popular::from)
                .toList();
    }

}
