package kr.hhplus.be.server.support.config.cache;

import kr.hhplus.be.server.domain.sales.ProductSalesCommand;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class CacheKeyConfig {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;

    @Bean
    public KeyGenerator popularProductsKeyGenerator() {
        return (target, method, params) -> {
            ProductSalesCommand.Popular cmd = (ProductSalesCommand.Popular) params[0];

            return String.format(
                    "%s-%s:top%d",
                    cmd.getStartDate().format(DATE_FMT),
                    cmd.getEndDate().format(DATE_FMT),
                    cmd.getTop());
        };
    }
}
