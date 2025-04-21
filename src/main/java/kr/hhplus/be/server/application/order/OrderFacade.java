package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final UserBalanceService userBalanceService;
    private final OrderDataPlatformClient orderDataPlatformClient;

    public OrderFacade(OrderService orderService,
                       ProductService productService,
                       PaymentService paymentService,
                       UserBalanceService userBalanceService,
                       OrderDataPlatformClient orderDataPlatformClient) {
        this.orderService = orderService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.userBalanceService = userBalanceService;
        this.orderDataPlatformClient = orderDataPlatformClient;
    }

    @Transactional
    public OrderResult order(OrderCriteria.Create criteria) {
        OrderCommand.Create createCommand = criteria.toCommand();

        // product 검증 및 재고 감소
        productService.validateAndSubStockProducts(createCommand.getCreateOrderItems());

        // order 생성, orderItem 저장, 쿠폰 적용
        OrderInfo orderInfo = orderService.create(createCommand);

        // pay 결제 생성
        PaymentCriteria paymentCriteria = PaymentCriteria.of(criteria.userId(), orderInfo.orderId(), orderInfo.finalPrice());
        paymentService.create(paymentCriteria.toPaymentCommand());

        // 잔액 차감
        userBalanceService.use(UserBalanceCommand.Use.of(paymentCriteria.userId(), paymentCriteria.finalPrice()));

        // 주문 확정
        orderInfo = orderService.confirmOrder(OrderCommand.Confirm.from(paymentCriteria.orderId()));

        // 데이터 플랫폼에 전송
        orderDataPlatformClient.sendOrderData(orderInfo);

        return OrderResult.from(orderInfo);
    }
}
