package kr.hhplus.be.server.support.contanier;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
        MySqlContainersConfig.class,
        RedisContainersConfig.class,
        KafkaContainersConfig.class
})
public abstract class TestContainerSupport {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        MySQLContainer<?> mySQLContainer = MySqlContainersConfig.getContainer();
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        GenericContainer<?> redisContainer = RedisContainersConfig.getContainer();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);

        KafkaContainer kafkaContainer = KafkaContainersConfig.getContainer();
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
