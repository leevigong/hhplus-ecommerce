package kr.hhplus.be.server.support.contanier;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class RedisContainersConfig implements BeforeAllCallback {

    public static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.0"))
                .withExposedPorts(6379);
        REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (REDIS_CONTAINER.isRunning()) return;

        REDIS_CONTAINER.start();
    }

    public static GenericContainer<?> getContainer() {
        return REDIS_CONTAINER;
    }
}
