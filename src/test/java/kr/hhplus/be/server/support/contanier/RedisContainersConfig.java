package kr.hhplus.be.server.support.contanier;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class RedisContainersConfig {

    public static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.0"))
                .withExposedPorts(6379);
        REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
    }

    @PreDestroy
    public void preDestroy() {
        if (REDIS_CONTAINER.isRunning()) REDIS_CONTAINER.stop();
    }
}
