package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
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
    public OrderResult order(OrderCriteria.Order criteria) {
        OrderCommand.Order orderCommand = criteria.toCommand();

        productService.validateProducts(orderCommand.getOrderItems());

        OrderInfo orderInfo = orderService.create(orderCommand);

        if (criteria.couponIssueId() != null) {
            orderInfo = orderService.applyCoupon(
                    OrderCommand.ApplyCoupon.of(orderInfo.orderId(), criteria.couponIssueId())
            );
        }

        PaymentCommand.Pay paymentCommand = PaymentCommand.Pay.of(
                criteria.userId(),
                orderInfo.orderId(),
                orderInfo.finalPrice()
        );
        paymentService.pay(paymentCommand);

        userBalanceService.use(UserBalanceCommand.Use.of(criteria.userId(), orderInfo.finalPrice()));

        orderInfo = orderService.confirmOrder(OrderCommand.Confirm.from(orderInfo.orderId()));

        return OrderResult.from(orderInfo);
    }
}
