package kr.hhplus.be.server.domain.sales;

import java.util.List;

public interface ProductSalesRepository {

    ProductSales save(ProductSales productSales);

    List<ProductSales> findPopulars(ProductSalesCommand.Popular command);
}
