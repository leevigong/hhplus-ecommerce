package kr.hhplus.be.server.support.contanier;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class RedisContainersConfig implements BeforeAllCallback, AfterAllCallback {

    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.0"))
                .withExposedPorts(6379);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.start();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.stop();
        }
    }

    public static GenericContainer<?> getContainer() {
        return REDIS_CONTAINER;
    }
}
