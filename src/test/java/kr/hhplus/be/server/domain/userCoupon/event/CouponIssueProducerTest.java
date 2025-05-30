package kr.hhplus.be.server.domain.userCoupon.event;

import kr.hhplus.be.server.support.kafka.KafkaTopics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponIssueProducerTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    CouponIssueProducer producer;

    @BeforeEach
    void setUp() {
        producer = new CouponIssueProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("카프카에 쿠폰 발급 요청 이벤트를 전송한다")
    void send_CouponIssueRequestEvent() {
        // given
        Long couponId = 1L;
        Long userId = 100L;

        // when
        producer.send(couponId, userId);

        // then
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(eq(KafkaTopics.COUPON_ISSUE_REQUESTED), keyCaptor.capture(), valueCaptor.capture());

        assertThat(keyCaptor.getValue()).isEqualTo(String.valueOf(couponId));
        assertThat(valueCaptor.getValue()).isInstanceOf(CouponIssueRequestEvent.class);

        CouponIssueRequestEvent event = (CouponIssueRequestEvent) valueCaptor.getValue();
        assertThat(event.couponId()).isEqualTo(couponId);
        assertThat(event.userId()).isEqualTo(userId);
    }
}
