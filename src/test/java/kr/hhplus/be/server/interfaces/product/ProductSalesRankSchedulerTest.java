package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductSalesRankSchedulerItTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSalesRankRepository productSalesRankRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductSalesRankScheduler scheduler;

    @Test
    void 최근3일간_판매기록을_기반으로_인기상품_랭킹을_저장한다() {
        // given
        Product product1 = productRepository.save(Product.builder().name("책1").price(1000).stockQuantity(100).build());
        Product product2 = productRepository.save(Product.builder().name("책2").price(2000).stockQuantity(100).build());

        // 주문 1: product1 3개
        OrderItem item1 = OrderItem.create(product1.getId(), 3, 1000);
        Order order1 = OrderFixture.create(1L, item1);
        orderRepository.save(order1);

        // 주문 2: product2 5개
        OrderItem item2 = OrderItem.create(product2.getId(), 5, 2000);
        Order order2 = OrderFixture.create(2L, item2);
        orderRepository.save(order2);

        // when
        scheduler.generate3DaySalesRank();

        // then
        List<ProductSalesRank> result = productSalesRankRepository.findByRankingScope(RankingScope.THREE_DAYS);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProduct().getId()).isEqualTo(product2.getId());
        assertThat(result.get(0).getTotalSalesCount()).isEqualTo(5);
        assertThat(result.get(0).getRankPosition()).isEqualTo(1);
    }

    public static class OrderFixture {
        public static Order create(Long userId, OrderItem... items) {
            return Order.create(userId, List.of(items));
        }
    }
}
