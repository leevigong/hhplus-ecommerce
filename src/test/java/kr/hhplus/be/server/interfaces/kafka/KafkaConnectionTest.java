package kr.hhplus.be.server.interfaces.kafka;

import kr.hhplus.be.server.support.contanier.TestContainerSupport;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class KafkaConnectionTest extends TestContainerSupport {

    private static final Logger log = LoggerFactory.getLogger(KafkaConnectionTest.class);

    private static final String TEST_TOPIC = "test-topic";
    private static final String TEST_KEY = "test-key";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

    @Test
    @DisplayName("Kafka 메시지 전송 – fire‑and‑forget")
    void sendMessageFireAndForget() {
        kafkaTemplate.send(TEST_TOPIC, TEST_KEY, "Hello hhplus!");
    }

    @Test
    @DisplayName("Kafka 메시지 전송 – 동기 전송")
    void sendMessageSynchronously() throws Exception {
        long offset = kafkaTemplate.send(TEST_TOPIC, TEST_KEY, "Hello Kafka!")
                .get()               // 동기 블로킹
                .getRecordMetadata()
                .offset();

        assertThat(offset).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Kafka 메타데이터 로그 출력")
    void loggingRecordMetadata() throws Exception {
        RecordMetadata recordMetadata = kafkaTemplate.send(TEST_TOPIC, TEST_KEY, "Kafka Log")
                .get()
                .getRecordMetadata();

        log.info("offset: {}", recordMetadata.offset());
        log.info("partition: {}", recordMetadata.partition());
        log.info("timestamp: {}", recordMetadata.timestamp());
        log.info("serializedKeySize: {}", recordMetadata.serializedKeySize());
        log.info("serializedValueSize: {}", recordMetadata.serializedValueSize());
        log.info("topic: {}", recordMetadata.topic());
    }


    @Test
    @DisplayName("Kafka 메시지 전송 실패 – 잘못된 토픽 이름(토픽 규칙 위반 문자 ! 사용)")
    void sendMessageShouldFailWithInvalidTopic() {
        String invalidTopic = "invalid!topic";
        assertThatThrownBy(() -> kafkaTemplate.send(invalidTopic, TEST_KEY, "Bad Message").get())
                .hasRootCauseInstanceOf(InvalidTopicException.class);
    }

    @Test
    @DisplayName("Kafka 리스너 메시지 수신 테스트")
    void kafkaListenerTest() throws InterruptedException {
        // given
        String testMessage = "Listener test message";
        CountDownLatch latch = testKafkaConsumer.prepareForNextMessage(); // 새 래치 준비 (다른 테스트 메시지 무시)

        // when
        kafkaTemplate.send(TEST_TOPIC, TEST_KEY, testMessage);

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(testKafkaConsumer.getMessage()).isEqualTo(testMessage);
    }

    @Test
    @DisplayName("동일 key 는 동일 파티션으로 라우팅된다")
    void sameKeyRoutesToSamePartition() throws Exception {
        // when
        var meta1 = kafkaTemplate.send(TEST_TOPIC, "same-key", "msg-1")
                .get()
                .getRecordMetadata();
        var meta2 = kafkaTemplate.send(TEST_TOPIC, "same-key", "msg-2")
                .get()
                .getRecordMetadata();

        // then
        assertThat(meta1.partition()).isEqualTo(meta2.partition());
    }
}
