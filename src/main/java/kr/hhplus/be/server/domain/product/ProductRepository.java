package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository {

    Product findById(Long id);
}
