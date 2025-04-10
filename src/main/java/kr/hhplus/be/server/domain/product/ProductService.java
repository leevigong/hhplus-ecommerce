package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.inferfaces.product.dto.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse.ProductV1 getProductById(Long productId) {
        Product product = productRepository.findById(productId);

        return ProductResponse.ProductV1.from(product);
    }
}
