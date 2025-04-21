package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.UserBalance;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceRepository;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        user = User.create("닉네임");
        userRepository.save(user);
        UserBalance userBalance = UserBalance.create(user, 0);
        userBalanceRepository.save(userBalance);

        // 상품 준비
        product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stockQuantity(10)
                .build());

        // 사용자 잔액 충전
        userBalanceService.charge(UserBalanceCommand.Charge.of(user.getId(), 50000));
    }

    @Test
    void 주문을_생성할_수_있다() {
        // given: 사용자와 상품 준비, 잔액 충전
        int quantity = 2;
        OrderCriteria.OrderItem orderItem = new OrderCriteria.OrderItem(
                product.getId(),
                quantity,
                product.getPrice()
        );

        OrderCriteria.Create 주문정보 = new OrderCriteria.Create(
                user.getId(),
                List.of(orderItem),
                null // 쿠폰 미적용
        );

        // 주문 전 초기 값 저장
        int initialStock = product.getStockQuantity();
        long initialBalance = userBalanceRepository.getByUserId(user.getId()).getBalance();

        // when: 주문 생성
        OrderResult result = orderFacade.order(주문정보);

        // then: 주문 상태, 재고 변화, 잔액 변화 검증

        // 1. 주문 상태 확인
        Order savedOrder = orderRepository.getById(result.orderId());
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.status()).isEqualTo("PAID");

        // 2. 주문 금액 확인
        long expectedTotalPrice = product.getPrice() * quantity;
        assertThat(result.totalPrice()).isEqualTo(expectedTotalPrice);
        assertThat(result.finalPrice()).isEqualTo(expectedTotalPrice); // 쿠폰 없으므로 할인 없음
        assertThat(result.discountAmount()).isEqualTo(0); // 할인 없음

        // 3. 재고 변화 확인
        Product updatedProduct = productRepository.getById(product.getId());
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(initialStock - quantity);

        // 4. 잔액 변화 확인
        UserBalance updatedBalance = userBalanceRepository.getByUserId(user.getId());
        assertThat(updatedBalance.getBalance()).isEqualTo(initialBalance - expectedTotalPrice);
    }
}
