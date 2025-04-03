package kr.hhplus.be.server.inferfaces.product.dto;

import kr.hhplus.be.server.domain.product.RankingScope;

public record ProductSalesRankResponse(
        Long productId,
        int salesCount,
        int totalSalesPrice,
        RankingScope rankingScope,
        int rankPosition
) {
}
