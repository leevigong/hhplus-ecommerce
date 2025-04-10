package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI EcommerceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce API")
                        .version("v1.0.0")
                        .description("이커머스 API 문서"));
    }
}
