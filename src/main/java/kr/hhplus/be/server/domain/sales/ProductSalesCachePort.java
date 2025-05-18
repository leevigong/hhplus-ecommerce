package kr.hhplus.be.server.domain.sales;

import kr.hhplus.be.server.domain.order.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface ProductSalesCachePort {

    void add(List<OrderItem> items);

    List<ProductSalesInfo.Popular> getTopSales(LocalDate date, int limit);

    List<ProductSalesInfo.Popular> getTopSalesRange(LocalDate startDate, LocalDate endDate, int top);

    List<ProductSalesInfo.Popular> getAllSales(LocalDate date);

}
