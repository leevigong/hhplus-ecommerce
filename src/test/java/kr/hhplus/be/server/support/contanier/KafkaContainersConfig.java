package kr.hhplus.be.server.support.contanier;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class KafkaContainersConfig implements BeforeAllCallback, AfterAllCallback {

    private static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
                .withReuse(true);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.start();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }

    public static KafkaContainer getContainer() {
        return KAFKA_CONTAINER;
    }
}
