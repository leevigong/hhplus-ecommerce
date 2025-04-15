package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final UserBalanceService userBalanceService;

    public OrderFacade(OrderService orderService,
                       PaymentService paymentService,
                       ProductService productService,
                       UserBalanceService userBalanceService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.productService = productService;
        this.userBalanceService = userBalanceService;
    }

    @Transactional
    public OrderResult order(OrderCriteria.Create criteria) {

        OrderCommand.Create createCommand = criteria.toCommand();
        // product 검증 및 재고 감소
        productService.validateAndSubStockProducts(createCommand.getCreateOrderItems());

        // order 생성, orderItem 저장
        OrderInfo orderInfo = orderService.create(createCommand);

        // 쿠폰 적용
        if (criteria.couponIssueId() != null) {
            orderInfo = orderService.applyCoupon(OrderCommand.ApplyCoupon.of(orderInfo.orderId(), criteria.couponIssueId()));
        }

        return OrderResult.from(orderInfo);
    }
}
