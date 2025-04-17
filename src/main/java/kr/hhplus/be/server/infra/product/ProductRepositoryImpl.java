package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product getById(Long id) {
        return productJpaRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_PRODUCT));
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }
}
