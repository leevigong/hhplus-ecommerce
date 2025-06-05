package kr.hhplus.be.server.support.preload;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.balance.*;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.sales.ProductSales;
import kr.hhplus.be.server.domain.sales.ProductSalesRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceHistoryRepository userBalanceHistoryRepository;
    private final ProductRepository productRepository;
    private final ProductSalesRankRepository productSalesRankRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductSalesRepository productSalesRepository;

    @PostConstruct
    @Transactional
    public void init() {
        for (int i = 1; i <= 100; i++) {
            User user = userRepository.save(User.create("테스트유저" + i));
            long initialBalance = 50_000; // 각 사용자 5만원 초기 잔액
            userBalanceRepository.save(UserBalance.create(user, initialBalance));
            userBalanceHistoryRepository.save(
                UserBalanceHistory.create(user.getId(), TransactionType.CHARGE, initialBalance, 0, initialBalance)
            );
        }

        // 상품 등록
        Product top = Product.create("상의", 19_800, 100, Category.TOP);
        Product bottom = Product.create("하의", 39_000, 80, Category.BOTTOM);
        Product shoes = Product.create("신발", 89_000, 50, Category.SHOES);
        List.of(top, bottom, shoes).forEach(productRepository::save);

        // 상품 판매 랭킹 등록
        List<ProductSalesRank> productSalesRanks = List.of(
                ProductSalesRank.create(top, 350, 6_930_000, RankingScope.THREE_DAYS, 1),
                ProductSalesRank.create(bottom, 120, 4_680_000, RankingScope.THREE_DAYS, 2),
                ProductSalesRank.create(shoes, 70, 6_230_000, RankingScope.THREE_DAYS, 3),
                ProductSalesRank.create(shoes, 150, 13_350_000, RankingScope.WEEKLY, 1),
                ProductSalesRank.create(top, 140, 2_772_000, RankingScope.WEEKLY, 2)
        );
        productSalesRanks.forEach(productSalesRankRepository::save);

        // 상품 판매
        List<ProductSales> productSalesList = List.of(
                ProductSales.createWithDate(1L, 999, LocalDate.now().minusDays(1)),
                ProductSales.createWithDate(2L, 500, LocalDate.now().minusDays(2)),
                ProductSales.createWithDate(3L, 1, LocalDate.now().minusDays(3))
        );
        productSalesList.forEach(productSalesRepository::save);

        // 쿠폰 및 사용자 쿠폰 등록 (첫 번째 사용자 기준)
        User firstUser = userRepository.findById(1L).orElseThrow();
        Coupon coupon = couponRepository.save(Coupon.createPercentage("TEST123", 10, 2000, LocalDateTime.now().plusDays(1)));
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.create(coupon, firstUser.getId()));

        // 주문 및 결제 등록 (첫 번째 사용자 기준)
        OrderItem orderItem = OrderItem.create(top.getId(), 1, 1000);
        Order order = orderRepository.save(Order.create(firstUser.getId(), List.of(orderItem)));
        paymentRepository.save(Payment.create(order.getId(), 10_000));
    }
}
