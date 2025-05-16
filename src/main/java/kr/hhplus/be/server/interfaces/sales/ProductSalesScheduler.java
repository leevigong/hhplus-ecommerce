package kr.hhplus.be.server.interfaces.sales;

import kr.hhplus.be.server.application.coupon.UserCouponFacade;
import kr.hhplus.be.server.domain.sales.ProductSales;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSalesScheduler {

    private final ProductSalesService productSalesService;

    @Scheduled(cron = "0 10 0 * * *")
    public void saveYesterdaySales() {
        log.info("전일 상품 판매 집계 저장 스케줄러 시작");
        try {
            productSalesService.saveYesterdaySales();
        } catch (Exception e) {
            log.error("전일 상품 판매 집계 저장 스케줄러 실행 중 오류 발생", e);
        }
    }
}
