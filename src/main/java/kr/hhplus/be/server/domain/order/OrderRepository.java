package kr.hhplus.be.server.domain.order;

public interface OrderRepository {

    Order save(Order order);

    Order getById(Long id);

}
