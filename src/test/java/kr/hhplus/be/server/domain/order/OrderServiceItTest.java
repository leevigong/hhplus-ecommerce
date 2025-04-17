package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrderServiceItTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = productRepository.save(Product.create("후드티", 5000, 100, Category.TOP));
    }

    @Test
    void 주문_생성_성공() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(product.getId(), 2, product.getPrice());
        List<OrderCommand.CreateOrderItem> orderItems = List.of(createOrderItem);
        OrderCommand.Create createCommand = new OrderCommand.Create(1L, null, orderItems);

        // when
        OrderInfo orderInfo = orderService.create(createCommand);

        // then
        Order order = orderRepository.getById(orderInfo.orderId());
        assertThat(order.getTotalPrice()).isEqualTo(10000); // 5000원 2개 구매
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void 주문_확정_성공() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(product.getId(), 2, product.getPrice());
        List<OrderCommand.CreateOrderItem> orderItems = List.of(createOrderItem);
        OrderCommand.Create createCommand = new OrderCommand.Create(1L, null, orderItems);
        OrderInfo orderInfo = orderService.create(createCommand);

        OrderCommand.Confirm confirmCommand = OrderCommand.Confirm.from(orderInfo.orderId());

        // when
        OrderInfo confirmedOrderInfo = orderService.confirmOrder(confirmCommand);

        // then
        Order order = orderRepository.getById(orderInfo.orderId());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }
}
