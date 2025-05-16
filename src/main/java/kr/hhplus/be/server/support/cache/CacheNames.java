package kr.hhplus.be.server.support.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {

    public static final String POPULAR_PRODUCTS = "popularProducts";
    public static final String PRODUCT = "product";
    public static final String POPULAR_PRODUCT_SALES = "product_sales:popular";
    public static final String COUPON_CANDIDATES = "candidates:coupon";

}
