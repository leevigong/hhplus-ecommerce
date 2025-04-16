package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PaymentFacade {

    private final PaymentService paymentService;
    private final UserBalanceService userBalanceService;
    private final OrderService orderService;

    public PaymentFacade(PaymentService paymentService,
                         UserBalanceService userBalanceService,
                         OrderService orderService) {
        this.paymentService = paymentService;
        this.userBalanceService = userBalanceService;
        this.orderService = orderService;
    }

    public void pay(PaymentCriteria criteria) {
        // pay 결제 생성
        paymentService.create(criteria.toPaymentCommand());

        // 잔액 차감
        userBalanceService.use(UserBalanceCommand.Use.of(criteria.userId(), criteria.finalPrice()));

        // 주문 확정
        orderService.confirmOrder(OrderCommand.Confirm.from(criteria.orderId()));
    }
}
