package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.RankingScope;
import kr.hhplus.be.server.inferfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.inferfaces.product.dto.ProductSalesRankResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSalesRankRepository productSalesRankRepository;

    public ProductService(ProductRepository productRepository, ProductSalesRankRepository productSalesRankRepository) {
        this.productRepository = productRepository;
        this.productSalesRankRepository = productSalesRankRepository;
    }

    public ProductResponse.ProductV1 getProductById(Long productId) {
        Product product = productRepository.findById(productId);

        return ProductResponse.ProductV1.from(product);
    }

    public List<ProductSalesRankResponse.ProductSalesRankV1> getProductSalesRank(String rankingScope) {
        List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

        return productSalesRanks.stream()
                .map(rank -> ProductSalesRankResponse.ProductSalesRankV1.from(rank))
                .collect(Collectors.toList());
    }
}

