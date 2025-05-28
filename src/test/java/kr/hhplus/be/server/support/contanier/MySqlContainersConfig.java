package kr.hhplus.be.server.support.contanier;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class MySqlContainersConfig implements BeforeAllCallback, AfterAllCallback {

    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("hhplus")
                .withUsername("test")
                .withPassword("test");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.start();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.stop();
        }
    }

    public static MySQLContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }
}
