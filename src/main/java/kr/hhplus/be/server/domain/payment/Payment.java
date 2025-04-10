package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private long amount;

    private Payment(Long orderId, long amount) {
        validatePaymentAmount(amount);

        this.orderId = orderId;
        this.amount = amount;
    }

    public static Payment create(Long orderId, long amount) {
        return new Payment(orderId, amount);
    }

    private void validatePaymentAmount(long amount) {
        if (amount <= 0) {
            throw new ApiException(ApiErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }
}
