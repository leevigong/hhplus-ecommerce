package kr.hhplus.be.server.infra.product;

public record ProductSalesDto(
        Long productId,
        long salesCount,
        long salesPrice
) {}
