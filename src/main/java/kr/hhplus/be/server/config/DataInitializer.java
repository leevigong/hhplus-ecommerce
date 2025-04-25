package kr.hhplus.be.server.config;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.balance.*;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.DiscountType;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @PostConstruct
    @Transactional
    public void init() {
        User user = userRepository.save(User.create("닉네임"));
        userBalanceRepository.save(UserBalance.create(user, 100_000));
        List<Product> products = List.of(
                Product.create("상의", 10_000, 10, Category.TOP),
                Product.create("바지", 20_000, 5, Category.BOTTOM),
                Product.create("신발", 30_000, 3, Category.SHOES)
        );
        userBalanceHistoryRepository.save(UserBalanceHistory.create(user.getId(), TransactionType.CHARGE, 100_000, 0, 100_000));

        products.forEach(productRepository::save);
        List<ProductSalesRank> productSalesRanks = List.of(
                ProductSalesRank.create(products.get(0), 10000, 500_000, RankingScope.THREE_DAYS, 1),
                ProductSalesRank.create(products.get(1), 500, 300_000, RankingScope.THREE_DAYS, 2),
                ProductSalesRank.create(products.get(2), 10, 100_000, RankingScope.THREE_DAYS, 3)
                );
        productSalesRanks.forEach(productSalesRankRepository::save);

        Coupon coupon = couponRepository.save(Coupon.create("TEST123", DiscountType.PERCENTAGE, 10, 100, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1)));
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.create(coupon, user.getId(), UserCouponStatus.AVAILABLE));

        OrderItem orderItem = OrderItem.create(products.get(0).getId(), 1, userCoupon.getId());
        Order order = orderRepository.save(Order.create(user.getId(), List.of(orderItem)));

        paymentRepository.save(Payment.create(order.getId(), 10_000));
    }
}
