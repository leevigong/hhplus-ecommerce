package kr.hhplus.be.server.domain.product;

public interface ProductRepository {

    Product getById(Long id);

    Product save(Product product);

}
