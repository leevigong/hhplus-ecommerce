package kr.hhplus.be.server.interfaces.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class TestKafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private String message;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void receive(String message) {
        System.out.println("메시지 수신 성공! 메시지: " + message);
        this.message = message;
        latch.countDown();          // 메시지 수신 시 Latch를 감소시킴
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getMessage() {
        return message;
    }

    public CountDownLatch prepareForNextMessage() {
        this.latch = new CountDownLatch(1);
        this.message = null;
        return this.latch;
    }
}
