package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.order.OrderCommand.Confirm;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(100L)
                .name("Test Product")
                .price(50)
                .stockQuantity(10)
                .category(Category.TOP)
                .build();

        order = Order.builder()
                .id(1L)
                .userId(1L)
                .totalPrice(100)
                .discountAmount(0)
                .finalPrice(100)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    void 주문_생성_성공() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(product.getId(), 2, product.getPrice());
        List<OrderCommand.CreateOrderItem> createOrderItems = List.of(createOrderItem);
        OrderCommand.Create createCommand = new OrderCommand.Create(1L, null, createOrderItems);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        OrderInfo orderInfo = orderService.create(createCommand);

        // then
        // 주문 항목의 총액: quantity 2 * price 50 = 100
        assertThat(orderInfo.totalPrice()).isEqualTo(100);
    }

    @Test
    void 주문_확정_성공() {
        // given
        Long orderId = 1L;
        when(orderRepository.getById(orderId)).thenReturn(order);

        Confirm confirmCommand = Confirm.from(orderId);

        // when
        OrderInfo orderInfo = orderService.confirmOrder(confirmCommand);

        // then
        assertThat(orderInfo.status()).isEqualTo(OrderStatus.PAID.name());
    }
}
