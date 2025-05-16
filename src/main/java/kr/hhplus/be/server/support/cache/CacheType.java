package kr.hhplus.be.server.support.cache;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheType implements Cacheable {

    POPULAR_PRODUCTS(CacheNames.POPULAR_PRODUCTS, Duration.ofHours(25)),
    PRODUCT(CacheNames.PRODUCT, Duration.ofDays(7)),
    POPULAR_PRODUCT_SALES(CacheNames.POPULAR_PRODUCT_SALES, Duration.ofDays(31))
    ;

    private final String cacheName;
    private final Duration ttl;

    CacheType(String cacheName, Duration ttl) {
        this.cacheName = cacheName;
        this.ttl = ttl;
    }
}
