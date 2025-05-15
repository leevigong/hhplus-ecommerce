package kr.hhplus.be.server.domain.sales;

import kr.hhplus.be.server.domain.order.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface ProductSalesRepository {

    ProductSales save(ProductSales productSales);

    List<ProductSales> findPopulars(ProductSalesCommand.Popular command);

    void add(List<OrderItem> items);

    List<ProductSalesInfo.Popular> getTopSalesRange(LocalDate startDate, LocalDate endDate, int top);
}
