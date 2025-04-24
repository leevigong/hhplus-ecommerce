package kr.hhplus.be.server.domain.product;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Product getById(Long id);

    Optional<Product> findByIdForUpdate(Long id);

}
