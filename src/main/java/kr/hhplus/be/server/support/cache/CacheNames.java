package kr.hhplus.be.server.support.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {

    public static final String POPULAR_PRODUCTS = "cache:popularProducts";
    public static final String PRODUCT = "cache:product";

}
